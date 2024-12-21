package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import java.util.regex.Pattern

object TipMessagesChatFilter : IChatFilter {
	private val alreadyTippedPattern = Pattern.compile(
		"You've already tipped someone in the past hour in [A-z ]+! Wait a bit and try again!"
	)
	private val tipMessages = listOf(
		"That player is not online, try another user!",
		"No one has a network booster active right now, Try again later.",
		"You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!",
		"Slow down! You can only use /tip every few seconds."
	)

	override val enabled: Boolean get() = config.hideTipMessages

	override fun shouldFilter(message: String): Boolean =
		alreadyTippedPattern.matches(message) ||
			message.startsWith("You tipped") ||
			message.startsWith("You were tipped") ||
			message in tipMessages
}