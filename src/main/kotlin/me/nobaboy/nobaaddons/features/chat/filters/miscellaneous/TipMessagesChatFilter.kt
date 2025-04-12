package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo

object TipMessagesChatFilter : IChatFilter {
	private val ALREADY_TIPPED_REGEX by Repo.regex(
		"filter.tips.already_tipped",
		"You've already tipped someone in the past hour in [A-z ]+! Wait a bit and try again!"
	)

	private val tipMessages by Repo.list(
		Repo.string("filter.tips.not_online", "That player is not online, try another user!"),
		Repo.string("filter.tips.no_boosters", "No one has a network booster active right now, Try again later."),
		Repo.string(
			"filter.tips.already_tipped_all",
			"You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!"
		),
		Repo.string("filter.tips.cooldown", "Slow down! You can only use /tip every few seconds.")
	)

	// TODO change these to regexes
	private val tipReceivedPrefix by Repo.string("filter.tips.received_prefix", "You were tipped")
	private val tipSentPrefix by Repo.string("filter.tips.sent_prefix", "You tipped")
	override val enabled: Boolean get() = config.hideTipMessages

	override fun shouldFilter(message: String): Boolean =
		ALREADY_TIPPED_REGEX.matches(message) ||
			message.startsWith(tipSentPrefix) ||
			message.startsWith(tipReceivedPrefix) ||
			message in tipMessages
}