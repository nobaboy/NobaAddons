package me.nobaboy.nobaaddons.features.rift

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.config.util.PerProfileDataLoader
import me.nobaboy.nobaaddons.utils.Timestamp

@Serializable
data class RiftTimerData(
	var freeInfusions: Int = 3,
	var nextFreeInfusion: Timestamp? = null,
	var nextSplitSteal: Timestamp? = null,
) {
	companion object {
		// loader needs to be exposed here to allow for migrating from legacy data
		internal val loader = PerProfileDataLoader<RiftTimerData>("rift_timers.json")
		val PROFILE by loader
	}
}