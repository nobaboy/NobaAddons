package me.nobaboy.nobaaddons.features.chat.filter.general

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Pattern

object TipMessageFilter {
	private val config get() = NobaConfigManager.get().chat.filter

	private val alreadyTippedPattern: Pattern = Pattern.compile("You've already tipped someone in the past hour in [A-z ]+! Wait a bit and try again!")
	private val tipMessages = listOf(
		"That player is not online, try another user!",
		"No one has a network booster active right now, Try again later.",
		"You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!",
		"Slow down! You can only use /tip every few seconds."
	)

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ -> processMessage(message.string.clean()) }
	}

	private fun processMessage(message: String): Boolean {
		if(!isEnabled()) return true

		val shouldFilter = alreadyTippedPattern.matches(message) ||
			message.startsWith("You tipped") ||
			message.startsWith("You were tipped") ||
			message in tipMessages

		// Inverted because false actually hides the message
		return !shouldFilter
	}

	private fun isEnabled() = config.hideTipMessages
}