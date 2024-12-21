package me.nobaboy.nobaaddons.repo

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import me.nobaboy.nobaaddons.repo.Repo.readJson
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class RepoObjectArray<T : Any>(val path: String, val cls: Class<T>) : IRepoObject {
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
		val stream = ByteArrayInputStream(json.toString().toByteArray()) // .close() is a noop, so no need to wrap in use { }
		return InputStreamReader(stream).use { Repo.GSON.fromJson(it, cls) }
	}

	override fun toString(): String = "RepoObjectArray(value=$instances, repoPath=$path, class=$cls)"

	companion object {
		/**
		 * Creates a [RepoObjectArray] instance supplying a [List] of instances of the target class
		 * from the mod's data repository
		 *
		 * The supplied list may be empty if the repository failed to load.
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
	}
}