package me.nobaboy.nobaaddons.repo

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.nobaboy.nobaaddons.repo.Repo.readJson
import kotlin.reflect.KProperty

/**
 * Shared constants storage for basic types like strings and regex patterns.
 */
object RepoValues {
	sealed class Values<T>(private val file: String) : IRepoObject {
		@Volatile private var values: Map<String, T> = mutableMapOf()

		protected abstract fun mapValues(entry: Map.Entry<String, JsonElement>): T

		override fun load() {
			values = Repo.REPO_DIRECTORY.resolve(file)
				.readJson(JsonObject::class.java)
				.asMap()
				.mapValues(this::mapValues)
		}

		operator fun get(key: String): T? = values[key]
	}

	/**
	 * Shared repo storage for [Regex] patterns
	 */
	object Regexes : Values<Regex>("data/regexes.json") {
		override fun mapValues(value: Map.Entry<String, JsonElement>): Regex = Regex(value.value.asString)
	}

	/**
	 * Shared repo storage for [String]s
	 */
	object Strings : Values<String>("data/strings.json") {
		override fun mapValues(value: Map.Entry<String, JsonElement>): String = value.value.asString
	}

	/**
	 * Deferred property supplier of a field from a repository [Values] instance
	 */
	class Entry<T>(private val key: String, private val fallback: T, private val supplier: Values<T>) {
		fun get(): T = supplier[key] ?: fallback
		operator fun getValue(instance: Any, property: KProperty<*>): T = get()
	}

	/**
	 * Deferred property supplier of a collection of [Entry] items
	 */
	class Entries<T>(private val entries: Collection<Entry<T>>) {
		fun get(): List<T> = entries.map { it.get() }
		operator fun getValue(instance: Any, property: KProperty<*>): List<T> = get()
	}
}