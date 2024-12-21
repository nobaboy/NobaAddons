package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.anyMatches
import java.util.regex.Pattern

object ProfileInfoChatFilter : IChatFilter {
	private val profileInfoPatterns = listOf(
		Pattern.compile("^You are playing on profile: [A-z-() ]+"),
		Pattern.compile("^Profile ID: [A-z0-9-]+")
	)

	override val enabled: Boolean get() = SkyBlockAPI.inSkyBlock && config.hideProfileInfo

	override fun shouldFilter(message: String): Boolean = profileInfoPatterns.anyMatches(message)
}