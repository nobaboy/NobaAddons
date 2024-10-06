package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons

object Utils {
	val onHypixel: Boolean
		get() = NobaAddons.mc.networkHandler?.serverInfo?.address?.endsWith(".hypixel.net") == true

	fun getPlayerName(): String? = NobaAddons.mc.player?.name?.string
}