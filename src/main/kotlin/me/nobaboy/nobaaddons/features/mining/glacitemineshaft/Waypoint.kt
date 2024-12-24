package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec

data class Waypoint(
	val location: NobaVec,
	// TODO Text
	val text: String,
	val color: NobaColor,
	var isCorpse: Boolean = false
)