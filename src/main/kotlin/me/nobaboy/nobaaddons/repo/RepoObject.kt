package me.nobaboy.nobaaddons.repo

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.repo.Repo.readJson
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class RepoObject<T : Any>(val file: String, val cls: Class<T>) : IRepoObject {
	@Volatile private var instance: T? = null

	@Suppress("UNCHECKED_CAST", "unused")
	operator fun getValue(instance: Any, property: KProperty<*>): T? = this.instance

	override fun load() {
		val file = Repo.REPO_DIRECTORY.resolve(this.file)
		if(file.exists()) {
			instance = file.readJson(cls)
		} else {
			NobaAddons.LOGGER.warn("Can't load repository file $file as it doesn't exist")
		}
	}

	override fun toString(): String {
		return "RepoObject(instance=$instance, repoFile=$file, class=$cls)"
	}

	companion object {
		/**
		 * Creates a new [RepoObject] supplying an instance of [T] loaded from the mod's repository
		 *
		 * Note that the supplied value will be nullable, as it may not be loaded yet (or potentially at all,
		 * depending on user conditions)
		 *
		 * ## Example
		 *
		 * ```kt
		 * // .json will be appended to the file name automatically if it isn't present
		 * val DATA by DataClass::class.fromRepository("feature/data")
		 * ```
		 */
		fun <T : Any> KClass<T>.fromRepository(file: String): RepoObject<T> {
			require(isData) { "The used class must be a data class" }
			return RepoObject(file.let { if(!it.endsWith(".json")) "$it.json" else it }, java).also(Repo::register)
		}
	}
}