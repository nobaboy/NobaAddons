package me.nobaboy.nobaaddons.config.util

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.core.PersistentCache
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import java.nio.file.Path
import java.util.Collections
import java.util.UUID
import kotlin.collections.iterator
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.reflect.KProperty

// TODO prune profiles that don't exist anymore (e.g. bingo profiles)
/**
 * Data loader for [ProfileData] classes
 *
 * ## Example
 *
 * ```kt
 * class ProfileData private constructor(profile: UUID?) : ProfileData(profile, "file.json") {
 *     var theAnswerToLifeTheUniverseAndEverything = 42
 *
 *     companion object {
 *         val PROFILE by ProfileDataLoader(::ProfileData)
 *     }
 * }
 * ```
 *
 * @see ProfileData
 */
class ProfileDataLoader<T : ProfileData>(private val constructor: (UUID?) -> T) {
	init {
		allLoaders.add(this)
		ClientLifecycleEvents.CLIENT_STOPPING.register { save() }
	}

	private val profiles = mutableMapOf<UUID?, T>()

	private val profileSwitchLock = Any()
	private val currentProfileId: UUID? by PersistentCache::lastProfile
	private lateinit var loaded: T

	/**
	 * The [UUID] of the loaded [currentProfile]
	 */
	val loadedProfileId: UUID? get() = if(::loaded.isInitialized) loaded.profile else null

	/**
	 * The loaded data for the current profile
	 */
	val currentProfile: T get() {
		synchronized(profileSwitchLock) {
			if(!::loaded.isInitialized || loadedProfileId != currentProfileId) {
				loaded = getOrPut(loadedProfileId)
				invokeLoadEvent()
			}
		}
		return loaded
	}

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): T = currentProfile

	private fun invokeLoadEvent() {
		SkyBlockEvents.PROFILE_DATA_LOADED.invoke(SkyBlockEvents.ProfileDataLoaded(loadedProfileId ?: return, loaded))
	}

	/**
	 * Save data for all currently loaded profiles to disk
	 */
	fun save() {
		for((profile, data) in profiles) {
			if(profile == null) continue
			PROFILES_DIR.resolve(profile.toString()).createDirectories()
			data.save()
		}
	}

	/**
	 * Get the relevant instance from this data loader for the given profile [id], creating one if necessary
	 */
	fun getOrPut(id: UUID?): T = profiles.getOrPut(id) {
		constructor(id).apply { if(id != null) safeLoad() }
	}

	companion object {
		val PROFILES_DIR: Path = NobaAddons.CONFIG_DIR.resolve("profiles")

		private val allLoaders = mutableListOf<ProfileDataLoader<*>>()
		val ALL_LOADERS: List<ProfileDataLoader<*>> = Collections.unmodifiableList(allLoaders)

		fun allProfiles(): Iterator<Pair<UUID, Path>> = iterator {
			val dirs = PROFILES_DIR.listDirectoryEntries().filter { it.isDirectory() }
			for(dir in dirs) {
				val uuid = runCatching { UUID.fromString(dir.name) }.getOrNull() ?: continue
				yield(uuid to dir)
			}
		}
	}
}
