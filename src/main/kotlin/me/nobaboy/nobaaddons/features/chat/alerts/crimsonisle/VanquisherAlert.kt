package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.alerts.ChatAlert
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr

object VanquisherAlert : ChatAlert(
	"vanquisher",
	tr("nobaaddons.feature.chatAlerts.crimsonIsle.vanquisher", "Vanquisher Spawn")
) {
	private val vanquisherSpawnMessage by "A Vanquisher is spawning nearby!".fromRepo("crimson_isle.vanquisher_spawn")

	override fun process(message: String) {
		if(!SkyBlockIsland.CRIMSON_ISLE.inIsland()) return
		if(!message.lowercaseEquals(vanquisherSpawnMessage)) return

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		ChatUtils.sendChatAsPlayer("$location | Vanquisher at [ ${SkyBlockAPI.prefixedZone} ] @$randomString")
	}
}
