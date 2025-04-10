package me.nobaboy.nobaaddons.config.profiles

import me.nobaboy.nobaaddons.config.util.ProfileData
import me.nobaboy.nobaaddons.config.util.ProfileDataLoader
import me.nobaboy.nobaaddons.utils.Timestamp
import java.util.UUID

class RiftTimerData private constructor(profile: UUID?) : ProfileData(profile, "rift_timers.json") {
	var freeInfusions: Int = 3
	var nextFreeInfusion: Timestamp? = null
	var nextSplitSteal: Timestamp? = null

	companion object {
		// loader needs to be exposed here to allow for migrating from legacy data
		internal val loader = ProfileDataLoader(::RiftTimerData)
		val PROFILE by loader
	}
}