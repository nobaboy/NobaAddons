package me.nobaboy.nobaaddons.utils

object HypixelUtils {
	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.serverInfo?.address?.endsWith(".hypixel.net") == true
}