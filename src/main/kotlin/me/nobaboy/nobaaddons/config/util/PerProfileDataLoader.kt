package me.nobaboy.nobaaddons.config.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.core.PersistentCache
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import java.nio.file.Path
import java.util.Collections
import java.util.UUID
import kotlin.collections.iterator
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.reflect.KProperty

// TODO prune profiles that don't exist anymore (e.g. bingo profiles)
/**
 * Data loader for a [kotlinx.serialization.Serializable] class on a per-profile basis.
 *
 * The provided class *must* be able to support loading an empty [kotlinx.serialization.json.JsonObject].
 *
 * ## Example
 *
 * ```kt
 * @Serializable
 * data class ProfileData(...) {
 *     companion object {
 *         val PROFILE by PerProfileDataLoader<ProfileData>("file.json")
 *     }
 * }
 */
class PerProfileDataLoader<T : Any>(private val serializer: KSerializer<T>, private val fileName: String) {
	init {
		allLoaders.add(this)
		ClientLifecycleEvents.CLIENT_STOPPING.register { saveAll() }
	}

	private val profiles = mutableMapOf<UUID?, T>()

	private val profileSwitchLock = Any()
	private val currentProfileId: UUID? by PersistentCache::lastProfile
	private lateinit var loaded: T

	/**
	 * The [UUID] of the loaded [currentProfile]
	 */
	var loadedProfileId: UUID? = null
		private set

	/**
	 * The loaded data for the current profile
	 */
	val currentProfile: T get() {
		synchronized(profileSwitchLock) {
			if(!::loaded.isInitialized || loadedProfileId != currentProfileId) {
				loadedProfileId = currentProfileId
				loaded = getOrPut(loadedProfileId)
				invokeLoadEvent()
			}
		}
		return loaded
	}

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): T = currentProfile

	operator fun set(key: UUID, value: T) {
		profiles.put(key, value)
		synchronized(profileSwitchLock) {
			if(key == currentProfileId) {
				loadedProfileId = key
				loaded = value
			}
		}
	}

	private fun invokeLoadEvent() {
		SkyBlockEvents.PROFILE_DATA_LOADED.invoke(SkyBlockEvents.ProfileDataLoad(loadedProfileId ?: return, loaded))
	}

	private fun file(id: UUID?) = PROFILES_DIR.resolve(id.toString()).resolve(fileName)

	/**
	 * Save data for all loaded profiles to disk
	 */
	fun saveAll() {
		for((profile, data) in profiles) {
			if(profile == null) continue
			val file = file(profile)
			file.createParentDirectories()
			file.toFile().writeAtomic(createBackup = false) { it.write(NobaAddons.JSON.encodeToString(serializer, data)) }
		}
	}

	private val empty: T get() = NobaAddons.JSON.decodeFromJsonElement(serializer, JsonObject(emptyMap()))

	private fun getOrPut(id: UUID?): T = profiles.getOrPut(id) {
		val file = file(id)

		if(id == null || !file.exists()) {
			return empty
		}

		safeLoad(file) {
			NobaAddons.JSON.decodeFromString(serializer, file.readText())
		}.getOrElse {
			NobaAddons.LOGGER.error("Failed to load profile data file '{}/{}'", id, fileName, it)
			empty
		}
	}

	companion object {
		val PROFILES_DIR: Path = NobaAddons.CONFIG_DIR.resolve("profiles")

		private val allLoaders = mutableListOf<PerProfileDataLoader<*>>()
		val ALL_LOADERS: List<PerProfileDataLoader<*>> = Collections.unmodifiableList(allLoaders)
	}
}

inline fun <reified T : Any> PerProfileDataLoader(fileName: String) =
	PerProfileDataLoader(serializer<T>(), fileName)
