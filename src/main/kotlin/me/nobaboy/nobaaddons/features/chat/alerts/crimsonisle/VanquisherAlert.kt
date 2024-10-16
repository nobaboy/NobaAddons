package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.api.SkyblockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text

object VanquisherAlert : IAlert {
	private val vanquisherSpawnMessage = "A Vanquisher is spawning nearby!"

	override fun shouldAlert(message: Text, text: String): Boolean {
		if(!text.lowercaseEquals(vanquisherSpawnMessage)) return false

		val player = MCUtils.player ?: return false
		val location = "x: ${player.x.toInt()}, y: ${player.y.toInt()}, z: ${player.z.toInt()}"
		ChatUtils.sendMessage("$location | Vanquisher at [${SkyblockAPI.currentZone}]")
		return true
	}

	override fun isEnabled(): Boolean = IslandType.CRIMSON_ISLE.inIsland() && config.vanquisherSpawn
}
