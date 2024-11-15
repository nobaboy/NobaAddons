package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.anyMatches
import net.minecraft.text.Text
import java.util.regex.Pattern

object ProfileInfoChatFilter : IChatFilter {
	private val profileInfoPatterns = listOf(
		Pattern.compile("^You are playing on profile: [A-z-() ]+"),
		Pattern.compile("^Profile ID: [A-z0-9-]+")
	)

	override fun shouldFilter(message: Text, text: String): Boolean = profileInfoPatterns.anyMatches(text)
	override fun isEnabled() = SkyBlockAPI.inSkyblock && config.hideProfileInfo
}