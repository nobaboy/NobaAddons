package me.nobaboy.nobaaddons.utils

import kotlinx.serialization.KSerializer
import me.nobaboy.nobaaddons.NobaAddons
import java.io.File
import java.io.IOException
import java.nio.file.Path

object FileUtils {
	@Throws(IOException::class)
	fun <T> File.readJson(serializer: KSerializer<T>): T =
		NobaAddons.JSON.decodeFromString(serializer, readText())

	@Throws(IOException::class)
	fun <T> File.readGson(type: Class<T>): T =
		bufferedReader().use { NobaAddons.GSON.fromJson(it, type) }

	@Throws(IOException::class)
	fun <T> Path.readJson(serializer: KSerializer<T>): T = toFile().readJson(serializer)

	@Throws(IOException::class)
	fun <T> Path.readGson(type: Class<T>): T = toFile().readGson(type)
}