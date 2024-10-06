package me.nobaboy.nobaaddons.features.chat.filter.general

import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.RegexUtils.anyMatches
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Pattern

object ProfileInfoFilter {
	private val config get() = NobaConfigManager.get().chat.filter

	private val profileInfoPatterns = listOf(
		Pattern.compile("^You are playing on profile: [A-z]+"),
		Pattern.compile("^Profile ID: [A-z0-9-]+")
	)

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ -> processMessage(message.string.clean()) }
	}

	private fun processMessage(message: String): Boolean {
		if(!isEnabled()) return true

		// Inverted because false actually hides the message
		return !profileInfoPatterns.anyMatches(message)
	}

	private fun isEnabled() = SkyblockAPI.inSkyblock && config.hideProfileInfo
}