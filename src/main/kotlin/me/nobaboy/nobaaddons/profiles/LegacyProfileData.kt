package me.nobaboy.nobaaddons.profiles

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.PerProfileDataLoader
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.features.rift.RiftTimerData
import java.util.UUID
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readText

// DO NOT ADD NEW THINGS TO THIS CLASS! This is now only used to migrate old profile data.
@Serializable
data class LegacyProfileData(
	var pet: PetData? = null,
	val riftTimers: RiftTimerData = RiftTimerData(),
	val trophyFish: MutableMap<String, MutableMap<TrophyFishRarity, Int>> = mutableMapOf(),
) {
	companion object {
		fun migrate() {
			val profiles = PerProfileDataLoader.PROFILES_DIR.listDirectoryEntries().filter { it.isDirectory() }
			profiles.forEach { dir ->
				val uuid = runCatching { UUID.fromString(dir.name) }.getOrNull() ?: return@forEach

				val data = dir.resolve("data.json")
				if(!data.exists()) return@forEach

				val loaded = runCatching {
					NobaAddons.JSON.decodeFromString(serializer(), data.readText())
				}.onFailure {
					NobaAddons.LOGGER.error("Failed to read old profile data file {}", data, it)
					return@forEach
				}.getOrThrow()

				SpawnedPetCache.loader[uuid] = SpawnedPetCache(loaded.pet)
				RiftTimerData.loader[uuid] = loaded.riftTimers
				TrophyFishCache.loader[uuid] = loaded.trophyFish

				data.toFile().renameTo(dir.resolve("data.json.migrated").toFile())
			}
			PerProfileDataLoader.ALL_LOADERS.forEach(PerProfileDataLoader<*>::saveAll)
		}
	}
}