package me.nobaboy.nobaaddons.features.chat.filter.general

import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.features.chat.filter.IFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.anyMatches
import net.minecraft.text.Text
import java.util.regex.Pattern

object ProfileInfoFilter : IFilter {
	private val profileInfoPatterns = listOf(
		Pattern.compile("^You are playing on profile: [A-z-() ]+"),
		Pattern.compile("^Profile ID: [A-z0-9-]+")
	)

	override fun shouldFilter(message: Text, text: String): Boolean = profileInfoPatterns.anyMatches(text)
	override fun isEnabled() = SkyblockAPI.inSkyblock && config.hideProfileInfo
}