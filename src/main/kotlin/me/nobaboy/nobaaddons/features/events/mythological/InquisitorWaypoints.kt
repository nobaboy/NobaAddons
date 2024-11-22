package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Pattern

class InquisitorWaypoints {
	private val config get() = NobaConfigManager.config.events.mythological

	private val inquisitorLocations = mutableListOf<NobaVec>()

	private val chatCoordsPattern = Pattern.compile(
		"(?i)(?:Party > )?(?<username>[A-z0-9_]+): x: (?<x>[0-9.-]+),? y: (?<y>[0-9.-]+),? z: (?<z>[0-9.-]+) | Inquisitor at.*"
	)

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { reset() }
		MythologicalEvents.INQUISITOR_SPAWN.register(this::onInquisitorSpawn)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onInquisitorSpawn(event: MythologicalEvents.InquisitorSpawn) {

	}

	private fun onChatMessage(message: String) {
		if(!isEnabled()) return

		chatCoordsPattern.matchMatcher(message) {

		}
	}

	private fun reset() {

	}

	private fun isEnabled() = DianaAPI.isActive() && config.alertInquisitor
}