package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.repo.RepoObject.Companion.fromRepository
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import java.util.regex.Pattern

object PickupObtainChatFilter : IChatFilter {
	private val itemPickupPattern by Regex("A (?<item>[A-z ]+) was picked up!").fromRepo("filter.pickup.item")
	private val playerObtainPattern by Regex("(?:\\[[A-Z+]+] )?[A-z0-9_]+ has obtained (?<item>[A-z ]+)!").fromRepo("filter.pickup.player_obtain")

	private val items by Items::class.fromRepository("filters/pickup")

	override val enabled = SkyBlockIsland.DUNGEONS.inIsland() && config.pickupObtainMessage

	override fun shouldFilter(message: String): Boolean {
		itemPickupPattern.onFullMatch(message) {
			return config.allowKeyMessage && groups["item"]!!.value !in (items?.allowed ?: emptySet())
		}

		playerObtainPattern.onFullMatch(message) {
			val item = groups["item"]!!.value

			return when {
				item in (items?.allowed ?: emptySet()) -> !config.allowKeyMessage
				item.startsWith("Blessing") || item in (items?.denied ?: emptySet()) -> true
				config.allow5050ItemMessage && item !in (items?.denied ?: emptySet()) -> false
				else -> false
			}
		}

		return false
	}

	private data class Items(val allowed: Set<String>, val denied: Set<String>)
}