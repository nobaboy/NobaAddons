package me.nobaboy.nobaaddons.core.profile

import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.rift.RiftTimerData
import java.util.UUID

class ProfileData private constructor(profile: UUID?) : AbstractPerProfileConfig(profile, "data.json") {
	var pet: PetData? = null
	var riftTimers: RiftTimerData = RiftTimerData()
	var trophyFish: MutableMap<String, MutableMap<TrophyFishRarity, Int>> = mutableMapOf()

	companion object : AbstractPerProfileDataLoader<ProfileData>() {
		override fun create(id: UUID?): ProfileData = ProfileData(id)

		override fun postLoad(id: UUID, data: ProfileData) {
			SkyBlockEvents.PROFILE_DATA_LOADED.invoke(SkyBlockEvents.ProfileDataLoad(id, data))
		}

		fun saveAll() {
			profiles.values.forEach { it.save() }
		}
	}
}