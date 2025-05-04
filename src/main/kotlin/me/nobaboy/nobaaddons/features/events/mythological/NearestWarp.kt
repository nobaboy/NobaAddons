package me.nobaboy.nobaaddons.features.events.mythological

data class NearestWarp(
	val warpPoint: BurrowWarpLocations.WarpPoint,
	var used: Boolean = false,
)