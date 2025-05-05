package me.nobaboy.nobaaddons.features.rift

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RiftTimerData(
	var freeInfusions: Int = 3,
	var nextFreeInfusion: Instant? = null,
	var nextSplitSteal: Instant? = null,
)