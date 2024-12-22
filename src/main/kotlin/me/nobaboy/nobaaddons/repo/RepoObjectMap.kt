package me.nobaboy.nobaaddons.repo

import com.google.gson.JsonElement
import me.nobaboy.nobaaddons.repo.Repo.readJson
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class RepoObjectMap<T>(private val path: String, private val cls: Class<T>) : IRepoObject {
	@Volatile private var instances: Map<String, T> = emptyMap()

	@Suppress("unused")
	operator fun getValue(instance: Any, property: KProperty<*>): Map<String, T> = instances

	override fun load() {
		val files = Repo.REPO_DIRECTORY.toPath().resolve(this.path).listDirectoryEntries("*.json")
		instances = files.associate { it.nameWithoutExtension to it.toFile().readJson(cls) }
	}

	override fun toString(): String = "RepoObjectDirectory(value=$instances, repoPath=$path, class=$cls)"

	companion object {
		/**
		 * Creates a [RepoObjectMap] instance supplying a [Map] of instances of the target class
		 * from the mod's data repository, reading individual JSON files from the provided directory
		 *
		 * The directory is not recursively loaded, and will only load `*.json` files at the top level.
		 *
		 * The supplied map may be empty if the repository failed to load.
		 *
		 * ## Example
		 *
		 * ```kt
		 * val ITEMS by DataClass::class.mapFromRepositoryDirectory("feature_name")
		 * ```
		 */
		fun <T : Any> KClass<T>.mapFromRepositoryDirectory(path: String): RepoObjectMap<T> {
			require(isData) { "The used class must be a data class" }
			return RepoObjectMap(path, java).also(Repo::register)
		}
	}
}