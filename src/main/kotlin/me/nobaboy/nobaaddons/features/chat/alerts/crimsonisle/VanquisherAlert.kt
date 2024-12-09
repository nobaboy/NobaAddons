package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

object VanquisherAlert : IAlert {
	private val vanquisherSpawnMessage = "A Vanquisher is spawning nearby!"

	override val enabled: Boolean get() = SkyBlockIsland.CRIMSON_ISLE.inIsland() && config.vanquisherSpawn

	override fun shouldAlert(message: String): Boolean {
		if(!message.lowercaseEquals(vanquisherSpawnMessage)) return false

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		ChatUtils.sendChatAsPlayer("$location | Vanquisher at [ ${SkyBlockAPI.prefixedZone} ] @$randomString")
		return true
	}
}
