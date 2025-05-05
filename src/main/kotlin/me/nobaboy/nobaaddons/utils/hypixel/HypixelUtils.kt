package me.nobaboy.nobaaddons.utils.hypixel

import me.nobaboy.nobaaddons.utils.mc.MCUtils

object HypixelUtils {
	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.brand?.startsWith("Hypixel BungeeCord") == true
}