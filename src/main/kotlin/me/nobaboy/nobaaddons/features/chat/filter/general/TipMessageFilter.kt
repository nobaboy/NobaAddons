package me.nobaboy.nobaaddons.features.chat.filter.general

import me.nobaboy.nobaaddons.features.chat.filter.IFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import net.minecraft.text.Text
import java.util.regex.Pattern

object TipMessageFilter : IFilter {
	private val alreadyTippedPattern: Pattern = Pattern.compile("You've already tipped someone in the past hour in [A-z ]+! Wait a bit and try again!")
	private val tipMessages = listOf(
		"That player is not online, try another user!",
		"No one has a network booster active right now, Try again later.",
		"You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!",
		"Slow down! You can only use /tip every few seconds."
	)

	override fun shouldFilter(message: Text, text: String): Boolean = alreadyTippedPattern.matches(text) ||
		text.startsWith("You tipped") ||
		text.startsWith("You were tipped") ||
		text in tipMessages

	override fun isEnabled() = config.hideTipMessages
}