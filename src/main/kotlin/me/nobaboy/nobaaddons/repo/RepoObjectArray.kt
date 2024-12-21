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
		val file = Repo.REPO_DIRECTORY.toPath().resolve(if(path.endsWith(".json")) path else "$path.json")
		val data = file.toFile().readJson(JsonArray::class.java)
		instances = data.map { adapt(it) }
	}

	/**
	 * Ugly hack to force GSON to load the correct type
	 */
	private fun adapt(json: JsonElement): T {
		val stream = ByteArrayInputStream(json.toString().toByteArray())
		return InputStreamReader(stream).use { Repo.GSON.fromJson(it, cls) }
	}

	override fun toString(): String {
		return "RepoObjectArray(value=$instances, repoPath=$path, class=$cls)"
	}

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
		fun <T : Any> KClass<T>.listFromRepository(path: String): RepoObjectArray<T> {
			require(isData) { "The used class must be a data class" }
			return RepoObjectArray(path, java).also(Repo::register)
		}
	}
}