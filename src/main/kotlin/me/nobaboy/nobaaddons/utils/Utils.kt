package me.nobaboy.nobaaddons.utils

object Utils {
	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.serverInfo?.address?.endsWith(".hypixel.net") == true
}