package me.nobaboy.nobaaddons.core

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigUtils.safeLoad
import me.nobaboy.nobaaddons.config.NobaConfigUtils.saveOnExit
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.rift.RiftTimerData
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.enumMap
import java.util.EnumMap
import java.util.UUID

class ProfileData private constructor(private val profile: UUID?) : AbstractConfig(
	NobaAddons.CONFIG_DIR.resolve("profiles").resolve("${profile ?: "unknown"}.json"),
	createIfMissing = profile != null,
) {
	init {
		if(profile != null) saveOnExit()
	}

	var pet by Property.ofNullable("pet", Serializer.expose<PetData>())
	val trophyFish by Property.of(
		key = "trophyFish",
		default = mutableMapOf(),
		serializer = Serializer.map<EnumMap<TrophyFishRarity, Int>>(Serializer.enumMap<TrophyFishRarity, Int>())
	)
	val riftTimers by RiftTimerData()

	private fun safeLoadAsync() {
		NobaAddons.runAsync {
			safeLoad()
			if(profile != null) {
				SkyBlockEvents.PROFILE_DATA_LOADED.invoke(SkyBlockEvents.ProfileDataLoad(profile, this@ProfileData))
			}
		}
	}

	companion object {
		init {
			SkyBlockEvents.PROFILE_CHANGE.register {
				PROFILES.putIfAbsent(it.profileId, ProfileData(it.profileId).also(ProfileData::safeLoadAsync))
			}
		}

		private val PROFILES = mutableMapOf<UUID?, ProfileData>()
		val PROFILE: ProfileData get() = getOrPut(SkyBlockAPI.currentProfile)

		private fun getOrPut(id: UUID?): ProfileData {
			return PROFILES.getOrPut(id) { ProfileData(id).also(ProfileData::safeLoadAsync) }
		}

		fun saveAll() {
			PROFILES.values.forEach { it.save() }
		}
	}
}