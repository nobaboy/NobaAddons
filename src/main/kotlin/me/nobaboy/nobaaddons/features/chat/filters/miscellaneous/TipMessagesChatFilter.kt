package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo

object TipMessagesChatFilter : IChatFilter {
	private val ALREADY_TIPPED_REGEX by Regex(
		"You've already tipped someone in the past hour in [A-z ]+! Wait a bit and try again!"
	).fromRepo("filter.tips.already_tipped")

	private val tipMessages by Repo.list(
		"That player is not online, try another user!".fromRepo("filter.tips.not_online"),
		"No one has a network booster active right now, Try again later.".fromRepo("filter.tips.no_boosters"),
		"You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!".fromRepo("filter.tips.already_tipped_all"),
		"Slow down! You can only use /tip every few seconds.".fromRepo("filter.tips.cooldown")
	)

	// TODO change these to regexes
	private val tipReceivedPrefix by "You were tipped".fromRepo("filter.tips.received_prefix")
	private val tipSentPrefix by "You tipped".fromRepo("filter.tips.sent_prefix")
	override val enabled: Boolean get() = config.hideTipMessages

	override fun shouldFilter(message: String): Boolean =
		ALREADY_TIPPED_REGEX.matches(message) ||
			message.startsWith(tipSentPrefix) ||
			message.startsWith(tipReceivedPrefix) ||
			message in tipMessages
}