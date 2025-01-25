package me.nobaboy.nobaaddons.core.slayer

import kotlinx.serialization.Serializable

@Serializable
data class SlayerData(
	val levelToXp: Map<String, List<Int>>,
)
