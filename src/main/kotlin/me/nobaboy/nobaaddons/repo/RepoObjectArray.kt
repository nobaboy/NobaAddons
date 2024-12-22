package me.nobaboy.nobaaddons.repo

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.nobaboy.nobaaddons.repo.Repo.readJson
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class RepoObjectArray<T : Any>(val path: String, val cls: Class<T>, private val adapter: ((JsonElement) -> T)? = null) : IRepoObject {
	@Volatile private var instances: List<T> = emptyList()

	@Suppress("unused")
	operator fun getValue(instance: Any, property: KProperty<*>): List<T> = instances

	override fun load() {
		instances = Repo.REPO_DIRECTORY.resolve(this.path)
			.readJson(JsonArray::class.java)
			.map { adapt(it) }
	}

	/**
	 * Loads the correct class type by wrapping the given [JsonElement] in a stream to re-load through GSON
	 */
	private fun adapt(json: JsonElement): T {
		if(adapter != null) return adapter(json)
		require(json is JsonObject) { "Non-object JSON values must use a custom adapter to read" }
		return super.adapt(json, cls)
	}

	override fun toString(): String = "RepoObjectArray(value=$instances, repoPath=$path, class=$cls)"

	companion object {
		/**
		 * Creates a [RepoObjectArray] instance supplying a [List] of instances of the target class
		 * from the mod's data repository
		 *
		 * The supplied list may be empty if the repository failed to load.
		 *
		 * The used clas **must** be a `data class`; you should provide an adapter instead if
		 * this isn't possible.
		 *
		 * ## Example
		 *
		 * ```kt
		 * val ITEMS by DataClass::class.listFromRepository("feature_name")
		 * ```
		 */
		fun <T : Any> KClass<T>.listFromRepository(file: String): RepoObjectArray<T> {
			require(isData) { "The used class must be a data class" }
			return RepoObjectArray(file, java).also(Repo::register)
		}

		/**
		 * Creates a [RepoObjectArray] supplying a [List] of instances of the target class, using the
		 * provided [adapter] to load values
		 *
		 * The supplied list may be empty if the repository failed to load.
		 *
		 * ## Example
		 *
		 * ```kt
		 * val ITEMS by String::class.listFromRepository("strings.json") { it.asString }
		 * ```
		 */
		fun <T : Any> KClass<T>.listFromRepository(file: String, adapter: (JsonElement) -> T): RepoObjectArray<T> {
			return RepoObjectArray(file, java, adapter).also(Repo::register)
		}
	}
}