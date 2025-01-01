package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.RegexUtils.anyFullMatch

object ProfileInfoChatFilter : IChatFilter {
	private val profileInfoPatterns: List<Regex> by Repo.list(
		Regex("^You are playing on profile: [A-z-() ]+").fromRepo("filter.profile.playing_on"),
		Regex("^Profile ID: [A-z0-9-]+").fromRepo("filter.profile.id"),
	)

	override fun shouldFilter(message: String): Boolean = profileInfoPatterns.anyFullMatch(message)
	override val enabled: Boolean get() = config.hideProfileInfo && SkyBlockAPI.inSkyBlock
}