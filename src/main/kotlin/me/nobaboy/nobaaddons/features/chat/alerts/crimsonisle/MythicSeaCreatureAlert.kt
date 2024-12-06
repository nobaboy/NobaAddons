package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SeaCreature
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text

object MythicSeaCreatureAlert : IAlert {
	override val enabled: Boolean get() = SkyBlockIsland.CRIMSON_ISLE.inIsland() && config.mythicSeaCreatureSpawn

	override fun shouldAlert(message: Text, text: String): Boolean {
		val seaCreature = SeaCreature.creatures[text]
		if(seaCreature != SeaCreature.THUNDER && seaCreature != SeaCreature.LORD_JAWBUS) return false

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		ChatUtils.sendChatAsPlayer("$location | ${seaCreature.displayName} at [ ${SkyBlockAPI.prefixedZone} ] @$randomString")

		return true
	}
}
