package me.nobaboy.nobaaddons.utils

object HypixelUtils {
	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.brand?.startsWith("Hypixel BungeeCord") == true
}