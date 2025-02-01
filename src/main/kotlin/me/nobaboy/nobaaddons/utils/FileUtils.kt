package me.nobaboy.nobaaddons.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.nobaboy.nobaaddons.NobaAddons
import net.minecraft.util.Util
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object FileUtils {
	/**
	 * Write to the given file atomically
	 *
	 * This creates a temporary file which is written to by [writer]; the current file is then
	 * renamed to `${name}_old` with the temporary file then moved to replace it.
	 *
	 * @see Util.backupAndReplace
	 */
	@Throws(IOException::class)
	fun File.writeAtomically(writer: (BufferedWriter) -> Unit) {
		val temp = Files.createTempFile("${nameWithoutExtension}-${StringUtils.randomAlphanumeric()}", extension)
		temp.toFile().bufferedWriter().use(writer)
		Util.backupAndReplace(temp, toPath(), toPath().parent.resolve("${name}_old"))
	}

	/**
	 * Create an instance of [T] from the current [File] using `kotlinx.serialization`
	 */
	@Throws(IOException::class)
	inline fun <reified T> File.readJson(json: Json = NobaAddons.JSON): T = json.decodeFromString(readText())

	/**
	 * Create an instance of [type] from the current [File] using Gson
	 */
	@Throws(IOException::class)
	fun <T> File.readGson(type: Class<T>): T = bufferedReader().use { NobaAddons.GSON.fromJson(it, type) }

	/**
	 * Dump the JSON representation of [obj] to the current [File] using `kotlinx.serialization`
	 *
	 * @see writeAtomically
	 */
	@Throws(IOException::class)
	inline fun <reified T> File.writeJson(obj: T, json: Json = NobaAddons.JSON) = writeAtomically {
		it.write(json.encodeToString(obj))
	}

	/**
	 * Dump the JSON representation of [obj] to the current [File] using Gson
	 *
	 * @see writeAtomically
	 */
	@Throws(IOException::class)
	fun <T> File.writeGson(obj: T) = writeAtomically { NobaAddons.GSON.toJson(obj, it) }

	/**
	 * Runs [with] with a temporary directory that is deleted once the provided function returns
	 */
	inline fun withTempDir(name: String, with: (Path) -> Unit) {
		val tmp = Files.createTempDirectory(name)
		try {
			with(tmp)
		} finally {
			tmp.recursiveDelete()
		}
	}

	/**
	 * Runs [with] with a file created in a temporary directory that is deleted once the provided function returns
	 *
	 * @see withTempDir
	 */
	inline fun withTempFile(dir: String, file: String, with: (File) -> Unit) {
		withTempDir(dir) {
			with(it.resolve(file).toFile().apply { createNewFile() })
		}
	}

	fun Path.recursiveDelete() = toFile().recursiveDelete()

	/**
	 * Deletes the current [File], recursively deleting everything in it if this is a directory.
	 */
	fun File.recursiveDelete() {
		if(isDirectory && !Files.isSymbolicLink(toPath())) {
			listFiles().forEach { it.recursiveDelete() }
		}
		delete()
	}
}