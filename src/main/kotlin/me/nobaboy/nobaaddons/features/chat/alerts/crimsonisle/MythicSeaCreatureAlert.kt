package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

object MythicSeaCreatureAlert : IAlert {
	override val enabled: Boolean get() = SkyBlockIsland.CRIMSON_ISLE.inIsland() && config.mythicSeaCreatureSpawn

	override fun shouldAlert(message: String): Boolean {
		val seaCreature = SeaCreature.getBySpawnMessage(message)
		if(seaCreature != SeaCreature.THUNDER && seaCreature != SeaCreature.LORD_JAWBUS) return false

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		ChatUtils.sendChatAsPlayer("$location | ${seaCreature.displayName} at [ ${SkyBlockAPI.prefixedZone} ] @$randomString")

		return true
	}
}
