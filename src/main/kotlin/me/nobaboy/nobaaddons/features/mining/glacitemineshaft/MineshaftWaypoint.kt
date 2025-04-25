package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.text.Text

data class MineshaftWaypoint(
	val location: NobaVec,
	val text: Text,
	val color: NobaColor,
	val type: MineshaftWaypointType,
)