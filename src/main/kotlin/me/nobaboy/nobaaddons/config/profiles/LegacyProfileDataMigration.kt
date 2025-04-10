package me.nobaboy.nobaaddons.config.profiles

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.ProfileDataLoader
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.config.util.safeLoad
import me.nobaboy.nobaaddons.utils.Timestamp
import kotlin.io.path.exists
import kotlin.io.path.moveTo
import kotlin.io.path.readText

// DO NOT ADD NEW THINGS TO THIS CLASS! This is now only used to migrate old profile data.
object LegacyProfileDataMigration {
	@Serializable
	private data class LegacyData(
		val pet: PetData? = null,
		val riftTimers: LegacyRiftTimerData = LegacyRiftTimerData(),
		val trophyFish: MutableMap<String, MutableMap<TrophyFishRarity, Int>> = mutableMapOf(),
	)

	@Serializable
	private data class LegacyRiftTimerData(
		val freeInfusions: Int = 3,
		val nextFreeInfusion: Timestamp? = null,
		val nextSplitSteal: Timestamp? = null,
	)

	fun migrate() {
		ProfileDataLoader.allProfiles().forEach { (uuid, dir) ->
			val data = dir.resolve("data.json")
			if(!data.exists()) return@forEach

			val loaded = safeLoad(data) {
				NobaAddons.JSON.decodeFromString(LegacyData.serializer(), data.readText())
			}.getOrNull() ?: return@forEach

			SpawnedPetCache.loader.getOrPut(uuid).pet = loaded.pet
			RiftTimerData.loader.getOrPut(uuid).apply {
				freeInfusions = loaded.riftTimers.freeInfusions
				nextFreeInfusion = loaded.riftTimers.nextFreeInfusion
				nextSplitSteal = loaded.riftTimers.nextSplitSteal
			}
			TrophyFishCache.loader.getOrPut(uuid).caught = loaded.trophyFish

			data.moveTo(dir.resolve("data.json.migrated"))
		}
		ProfileDataLoader.ALL_LOADERS.forEach(ProfileDataLoader<*>::save)
	}
}