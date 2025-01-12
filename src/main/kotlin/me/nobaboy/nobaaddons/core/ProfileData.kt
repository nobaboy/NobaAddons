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

class ProfileData private constructor(val profile: UUID?) : AbstractConfig(
	NobaAddons.CONFIG_DIR.resolve("profiles").resolve("${profile ?: "unknown"}.json"),
	createIfMissing = profile != null,
) {
	init {
		saveOnExit(onlyIfDirty = profile == null)
	}

	var pet by Property.ofNullable("pet", Serializer.expose<PetData>())
	val trophyFish by Property.of(
		key = "trophyFish",
		default = mutableMapOf(),
		serializer = Serializer.map<EnumMap<TrophyFishRarity, Int>>(Serializer.enumMap<TrophyFishRarity, Int>())
	)
	val riftTimers by RiftTimerData()

	companion object {
		init {
			SkyBlockEvents.PROFILE_CHANGE.register { getOrPut(it.profileId) }
		}

		private val PROFILES = mutableMapOf<UUID?, ProfileData>()
		private var _profile: ProfileData? = null

		val PROFILE: ProfileData get() {
			var current = _profile?.takeIf { it.profile == SkyBlockAPI.currentProfile }
			if(current == null) {
				current = getOrPut(SkyBlockAPI.currentProfile)
				_profile = current
			}
			return current
		}

		private fun getOrPut(id: UUID?): ProfileData {
			return PROFILES.getOrPut(id) {
				val data = ProfileData(id)
				data.safeLoad()
				if(id != null) {
					SkyBlockEvents.PROFILE_DATA_LOADED.invoke(SkyBlockEvents.ProfileDataLoad(id, data))
				}
				data
			}
		}

		fun saveAll() {
			PROFILES.values.forEach { it.save() }
		}
	}
}