package me.nobaboy.nobaaddons.repo

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import me.nobaboy.nobaaddons.repo.Repo.readJson
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class RepoObjects<T : Any>(val path: String, val cls: Class<T>) : IRepoObject {
	@Volatile private var instances: List<T> = emptyList()

	@Suppress("unused")
	operator fun getValue(instance: Any, property: KProperty<*>): List<T> = instances

	override fun load() {
		val path = Repo.REPO_DIRECTORY.toPath().resolve(path)
		if(path.isDirectory()) {
			loadFromDirectory(path)
		} else {
			loadFromArray(path)
		}
	}

	private fun loadFromDirectory(dir: Path) {
		require(dir.isDirectory()) { "Cannot load from $dir as it is not a directory" }
		val files = dir.listDirectoryEntries("*.json")
		instances = files.map { it.toFile().readJson(cls) }
	}

	/**
	 * Ugly hack to force GSON to load the correct type
	 */
	private fun adapt(json: JsonElement): T {
		ByteArrayInputStream(json.toString().toByteArray()).use {
			InputStreamReader(it).use { return Repo.GSON.fromJson(it, cls) }
		}
	}

	private fun loadFromArray(file: Path) {
		val data = file.toFile().readJson(JsonArray::class.java)
		instances = data.map { adapt(it) }
	}

	override fun toString(): String {
		return "RepoObjects(value=$instances, repoPath=$path, class=$cls)"
	}

	companion object {
		/**
		 * Creates a [RepoObjects] instance supplying a [List] of instances of the target class
		 * from the mod's data repository
		 *
		 * The data may be stored in either a directory containing JSON files, or a JSON file with an array
		 * of objects; directories are only recursed one level deep (meaning no subdirectories).
		 *
		 * Note that unlike [RepoObject], the path **must** end in `.json` if the path is being pointed
		 * at an individual JSON file.
		 *
		 * The supplied list may be empty if the repository failed to load.
		 *
		 * ## Example
		 *
		 * ```kt
		 * val ITEMS by DataClass::class.listFromRepository("feature_name")
		 * ```
		 */
		fun <T : Any> KClass<T>.listFromRepository(path: String): RepoObjects<T> {
			require(isData) { "The used class must be a data class" }
			return RepoObjects(path, java).also(Repo::register)
		}
	}
}