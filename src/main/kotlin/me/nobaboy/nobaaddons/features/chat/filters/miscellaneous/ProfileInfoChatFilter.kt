package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.RegexUtils.anyFullMatch
import net.minecraft.text.Text

object ProfileInfoChatFilter : IChatFilter {
	private val profileInfoPatterns: List<Regex> by Repo.list(
		Repo.regex("filter.profile.playing_on", "^You are playing on profile: [A-z-() ]+"),
		Repo.regex("filter.profile.uuid", "^Profile ID: [A-z0-9-]+"),
	)

	private val UUID_SUGGEST_REGEX by Repo.regex(
		"filter.profile.uuid_suggest",
		"^§e§lCLICK THIS TO SUGGEST IT IN CHAT §7§l\\[(?:NO )?DASHES]"
	)

	private fun isSuggestProfile(message: Text): Boolean {
		if(UUID_SUGGEST_REGEX.matches(message.string)) {
			val clickAction = message.style.clickEvent ?: return false
			return CommonPatterns.UUID_REGEX.matches(clickAction.value)
		}
		return false
	}

	override fun shouldFilter(message: Text): Boolean {
		return isSuggestProfile(message) || super.shouldFilter(message)
	}

	override fun shouldFilter(message: String): Boolean = profileInfoPatterns.anyFullMatch(message)
	override val enabled: Boolean get() = config.hideProfileInfo && SkyBlockAPI.inSkyBlock
}