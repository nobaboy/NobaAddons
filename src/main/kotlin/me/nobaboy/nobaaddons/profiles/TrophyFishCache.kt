package me.nobaboy.nobaaddons.profiles

import me.nobaboy.nobaaddons.config.util.PerProfileDataLoader
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity

object TrophyFishCache {
	// loader needs to be exposed here to allow for migrating from legacy data
	internal val loader = PerProfileDataLoader<MutableMap<String, MutableMap<TrophyFishRarity, Int>>>("trophy_fish.json")
	val PROFILE by loader
}
