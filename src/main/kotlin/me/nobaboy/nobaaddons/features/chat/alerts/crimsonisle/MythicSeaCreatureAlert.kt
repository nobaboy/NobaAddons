package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.core.SeaCreature
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text

object MythicSeaCreatureAlert : IAlert {
	override fun shouldAlert(message: Text, text: String): Boolean {
		val seaCreature = SeaCreature.creatures[text]
		if(seaCreature != SeaCreature.THUNDER && seaCreature != SeaCreature.LORD_JAWBUS) return false

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		ChatUtils.sendChatAsPlayer("$location | ${seaCreature.displayName} at ${SkyBlockAPI.currentZone} @$randomString")

		return true
	}

	override fun isEnabled(): Boolean = IslandType.CRIMSON_ISLE.inIsland() && config.mythicSeaCreatureSpawn
}
