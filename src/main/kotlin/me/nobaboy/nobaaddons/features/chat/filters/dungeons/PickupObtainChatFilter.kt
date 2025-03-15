package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch

object PickupObtainChatFilter : IChatFilter {
	private val ITEM_PICKUP_REGEX by Regex("A (?<item>[A-z ]+) was picked up!").fromRepo("filter.pickup.item")
	private val ITEM_OBTAINED_REGEX by Regex("(?:\\[[A-Z+]+] )?[A-z0-9_]+ has obtained (?<item>[A-z ]+)!").fromRepo("filter.pickup.item_obtained")

	private val items by Repo.create("filters/pickup.json", Items.serializer())

	override val enabled: Boolean get() = config.pickupObtainMessage && SkyBlockIsland.DUNGEONS.inIsland()

	override fun shouldFilter(message: String): Boolean {
		ITEM_PICKUP_REGEX.onFullMatch(message) {
			return config.allowKeyMessage && groups["item"]!!.value !in (items?.allowed ?: emptySet())
		}

		ITEM_OBTAINED_REGEX.onFullMatch(message) {
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

	@Serializable
	private data class Items(val allowed: Set<String>, val denied: Set<String>)
}