package me.nobaboy.nobaaddons.core.profile

import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.rift.RiftTimerData
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.enumMap
import java.util.UUID

class ProfileData private constructor(profile: UUID?) : AbstractPerProfileConfig(profile, "data.json") {
	var pet by Property.ofNullable("pet", Serializer.expose<PetData>())
	val riftTimers by RiftTimerData()
	val trophyFish by Property.of(
		"trophyFish",
		Serializer.map(Serializer.enumMap<TrophyFishRarity, Int>()),
		mutableMapOf()
	)

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