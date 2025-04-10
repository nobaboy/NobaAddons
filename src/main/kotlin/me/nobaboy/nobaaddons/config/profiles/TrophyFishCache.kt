package me.nobaboy.nobaaddons.config.profiles

import me.nobaboy.nobaaddons.config.util.ProfileDataLoader
import me.nobaboy.nobaaddons.config.util.ProfileData
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import java.util.UUID

class TrophyFishCache private constructor(profile: UUID?) : ProfileData(profile, "trophy_fish.json") {
	var caught = mutableMapOf<String, MutableMap<TrophyFishRarity, Int>>()

	companion object {
		// loader needs to be exposed here to allow for migrating from legacy data
		internal val loader = ProfileDataLoader(::TrophyFishCache)
		val PROFILE by loader
	}
}
