package me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.features.chat.alerts.IAlert
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatchers
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text
import java.util.regex.Pattern

object MythicSeaCreatureAlert : IAlert {
	private val mythicSeaCreaturePatterns = listOf<Pattern>(
		Pattern.compile("^You have angered a legendary creature... (?<creature>[A-z ]+) has arrived."),
		Pattern.compile("^You hear a massive rumble as (?<creature>[A-z ]+) emerges.")
	)

	override fun shouldAlert(message: Text, text: String): Boolean {
		mythicSeaCreaturePatterns.matchMatchers(text) {
			val player = MCUtils.player ?: return false
			val creature = group("creature")
			val location = "x: ${player.x.toInt()}, y: ${player.y.toInt()}, z: ${player.z.toInt()}"
			val randomString = StringUtils.randomAlphanumeric()
			ChatUtils.sendMessage("$location | $creature at ${SkyBlockAPI.currentZone} @$randomString")
			return true
		}
		return false
	}

	override fun isEnabled(): Boolean = IslandType.CRIMSON_ISLE.inIsland() && config.mythicSeaCreatureSpawn
}
