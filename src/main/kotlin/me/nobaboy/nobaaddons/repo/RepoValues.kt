package me.nobaboy.nobaaddons.repo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.repo.serializers.RegexKSerializer
import net.fabricmc.loader.api.FabricLoader
import kotlin.reflect.KProperty

/**
 * Shared constants storage for common types like strings and regex patterns.
 */
object RepoValues {
	sealed class Values<T>(private val file: String, private val knownKeys: Set<String>?, private val valueSerializer: KSerializer<T>) : IRepoObject {
		@Volatile private var values: Map<String, T> = mutableMapOf()

		override fun load() {
			val data = Repo.readAsJson(file) as JsonObject
			values = data.mapValues { Repo.JSON.decodeFromJsonElement(valueSerializer, it.value) }
			if(FabricLoader.getInstance().isDevelopmentEnvironment) warnMissingRepoKeys()
		}

		private fun warnMissingRepoKeys() {
			if(knownKeys == null) return
			knownKeys.forEach {
				if(it !in values) NobaAddons.LOGGER.error("Key {} isn't present in repo", it)
			}
		}

		operator fun get(key: String): T? = values[key]
		override fun toString(): String = "${this::class.simpleName}($values)"
	}

	/**
	 * Shared repo storage for [Regex] patterns
	 */
	object Regexes : Values<Regex>("data/regexes.json", Repo.knownRegexKeys, RegexKSerializer)

	/**
	 * Shared repo storage for [String]s
	 */
	object Strings : Values<String>("data/strings.json", Repo.knownStringKeys, serializer())

	/**
	 * Deferred property supplier of a field from a repository [Values] instance
	 */
	class Entry<T>(private val key: String, private val fallback: T, private val supplier: Values<T>) {
		fun get(): T = supplier[key] ?: fallback
		@Suppress("unused") operator fun getValue(instance: Any?, property: KProperty<*>): T = get()
	}

	/**
	 * Deferred property supplier of a collection of [Entry] items
	 */
	class Entries<T>(private val entries: Collection<Entry<T>>) {
		fun get(): List<T> = entries.map { it.get() }
		@Suppress("unused") operator fun getValue(instance: Any?, property: KProperty<*>): List<T> = get()
	}
}