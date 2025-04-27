package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.RegexUtils.anyFullMatch
import net.minecraft.text.Text

object ProfileInfoChatFilter : IChatFilter {
	private val PROFILE_INFO_REGEXES by Repo.list(
		Regex("^You are playing on profile: [A-z-() ]+").fromRepo("filter.profile.playing_on"),
		Regex("^Profile ID: [A-z0-9-]+").fromRepo("filter.profile.uuid"),
	)

	private val UUID_SUGGEST_REGEX by Regex("^§e§lCLICK THIS TO SUGGEST IT IN CHAT §7§l\\[(?:NO )?DASHES]").fromRepo("filter.profile.uuid_suggest")

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

	override fun shouldFilter(message: String): Boolean = PROFILE_INFO_REGEXES.anyFullMatch(message)
	override val enabled: Boolean get() = config.hideProfileInfo && SkyBlockAPI.inSkyBlock
}