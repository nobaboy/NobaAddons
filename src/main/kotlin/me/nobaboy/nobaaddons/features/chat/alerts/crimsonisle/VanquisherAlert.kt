package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text

object VanquisherAlert : IAlert {
	private val vanquisherSpawnMessage = "A Vanquisher is spawning nearby!"

	override fun shouldAlert(message: Text, text: String): Boolean {
		if(!text.lowercaseEquals(vanquisherSpawnMessage)) return false

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		ChatUtils.sendChatAsPlayer("$location | Vanquisher at ${SkyBlockAPI.currentZone} @$randomString")
		return true
	}

	override fun isEnabled(): Boolean = IslandType.CRIMSON_ISLE.inIsland() && config.vanquisherSpawn
}
