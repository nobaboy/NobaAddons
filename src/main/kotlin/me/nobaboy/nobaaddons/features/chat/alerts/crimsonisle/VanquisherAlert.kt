package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

object VanquisherAlert : IAlert {
	private val vanquisherSpawnMessage by "A Vanquisher is spawning nearby!".fromRepo("crimson_isle.vanquisher_spawn")

	override val enabled: Boolean get() = config.vanquisherSpawn && SkyBlockIsland.CRIMSON_ISLE.inIsland()

	override fun shouldAlert(message: String): Boolean {
		if(message != vanquisherSpawnMessage) return false

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		ChatUtils.sendChatAsPlayer("$location | Vanquisher at [ ${SkyBlockAPI.prefixedZone} ] @$randomString")
		return true
	}
}
