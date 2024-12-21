package me.nobaboy.nobaaddons.repo

import me.nobaboy.nobaaddons.repo.Repo.readJson
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class RepoObject<T : Any>(val path: String, val cls: Class<T>) : IRepoObject {
	@Volatile private var instance: T? = null

	@Suppress("unused")
	operator fun getValue(instance: Any, property: KProperty<*>): T? = this.instance

	override fun load() {
		instance = Repo.REPO_DIRECTORY.resolve(path).readJson(cls)
	}

	override fun toString(): String = "RepoObject(value=$instance, repoPath=$path, class=$cls)"

	companion object {
		/**
		 * Creates a new [RepoObject] supplying an instance of [T] loaded from the mod's repository
		 *
		 * The supplied value may be null if the repository failed to load.
		 *
		 * ## Example
		 *
		 * ```kt
		 * val DATA by DataClass::class.fromRepository("feature/data.json")
		 * ```
		 */
		fun <T : Any> KClass<T>.fromRepository(file: String): RepoObject<T> {
			require(isData) { "The used class must be a data class" }
			return RepoObject(file, java).also(Repo::register)
		}
	}
}