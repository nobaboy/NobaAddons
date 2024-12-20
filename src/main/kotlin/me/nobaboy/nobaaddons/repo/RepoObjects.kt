package me.nobaboy.nobaaddons.repo

import me.nobaboy.nobaaddons.repo.Repo.readJson
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class RepoObjects<T : Any>(val directory: String, val cls: Class<T>) : IRepoObject {
	@Volatile private var instances: Map<String, Any> = mapOf()

	@Suppress("UNCHECKED_CAST", "unused")
	operator fun getValue(instance: Any, property: KProperty<*>): Map<String, T> = instances as Map<String, T>

	override fun load() {
		val files = Repo.REPO_DIRECTORY.toPath().resolve(directory).listDirectoryEntries("*.json")
		instances = files.associate {
			it.name.replace(".json", "") to it.toFile().readJson(cls)
		}
	}

	override fun toString(): String {
		return "RepoObjects(value=$instances, repoPath=$directory, class=$cls)"
	}

	companion object {
		/**
		 * Creates a [RepoObjects] instance supplying a [Map] of file names to objects of the class
		 * from the mod's repository
		 *
		 * Note that this does not descend into subdirectories.
		 *
		 * ## Example
		 *
		 * ```kt
		 * val ITEMS by DataClass::class.fromRepoDirectory("feature_name")
		 * ```
		 */
		fun <T : Any> KClass<T>.fromRepositoryDirectory(directory: String): RepoObjects<T> {
			require(isData) { "The used class must be a data class" }
			return RepoObjects(directory, java).also(Repo::register)
		}
	}
}