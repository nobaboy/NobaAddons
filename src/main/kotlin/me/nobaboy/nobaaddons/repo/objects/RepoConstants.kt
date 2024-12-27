package me.nobaboy.nobaaddons.repo.objects

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.events.RepoReloadEvent
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.RepoManager
import me.nobaboy.nobaaddons.repo.serializers.RegexKSerializer
import net.fabricmc.loader.api.FabricLoader
import kotlin.reflect.KProperty

/**
 * Shared constants storage for common types like strings and regex patterns.
 */
object RepoConstants {
	sealed class Handler<T>(private val file: String, private val valueSerializer: KSerializer<T>) : IRepoObject {
		@Volatile private var values: Map<String, T> = mutableMapOf()
		val entries: MutableMap<String, Entry<T>> = mutableMapOf()

		init {
			RepoManager.performInitialLoad(this)
			RepoReloadEvent.EVENT.register { this.load() }
		}

		override fun load() {
			val data = Repo.readAsJson(file) as JsonObject
			values = data.mapValues { Repo.JSON.decodeFromJsonElement(valueSerializer, it.value) }
			if(FabricLoader.getInstance().isDevelopmentEnvironment) warnMissingRepoKeys()
		}

		private fun warnMissingRepoKeys() {
			entries.keys.forEach {
				if(it !in values) NobaAddons.LOGGER.error("Key {} isn't present in repo", it)
			}
			entries.forEach { id, it ->
				if(it.overriddenByRemote) NobaAddons.LOGGER.warn("Value for '{}' has been overridden by the repo", id)
			}
		}

		operator fun get(key: String): T? = values[key]
		override fun toString(): String = "${this::class.simpleName}($values)"
	}

	/**
	 * Shared repo storage for [Regex] patterns
	 */
	object Regexes : Handler<Regex>("data/regexes.json", RegexKSerializer)

	/**
	 * Shared repo storage for [String]s
	 */
	object Strings : Handler<String>("data/strings.json", serializer())

	private val LOCK = Any()

	/**
	 * Deferred property supplier of a field from a repository [Handler] instance
	 */
	class Entry<T>(val key: String, val fallback: T, val supplier: Handler<T>) {
		init {
			synchronized(LOCK) {
				if(key in supplier.entries && supplier.entries[key]!!.fallback != fallback) {
					throw IllegalArgumentException("Detected invalid key reuse: $key has already been used with a different value")
				}
				supplier.entries[key] = this
			}
		}

		val overriddenByRemote: Boolean get() {
			val current = supplier[key] ?: return false
			if(current is Regex && fallback is Regex) {
				return current.pattern != fallback.pattern
			}
			return current != fallback
		}

		fun get(): T = supplier[key] ?: fallback
		@Suppress("unused") operator fun getValue(instance: Any?, property: KProperty<*>): T = get()
	}

	/**
	 * Deferred property supplier of a collection of [Entry] items
	 */
	class Entries<T>(val entries: Collection<Entry<T>>) {
		fun get(): List<T> = entries.map { it.get() }
		@Suppress("unused") operator fun getValue(instance: Any?, property: KProperty<*>): List<T> = get()
	}
}