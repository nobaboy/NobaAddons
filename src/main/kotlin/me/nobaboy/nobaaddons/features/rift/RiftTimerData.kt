package me.nobaboy.nobaaddons.features.rift

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.utils.Timestamp

@Serializable
data class RiftTimerData(
	var freeInfusions: Int = 3,
	var nextFreeInfusion: Timestamp? = null,
	var nextSplitSteal: Timestamp? = null,
)