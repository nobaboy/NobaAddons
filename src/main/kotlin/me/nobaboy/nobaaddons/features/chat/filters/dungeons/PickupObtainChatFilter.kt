package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import java.util.regex.Pattern

object PickupObtainChatFilter : IChatFilter {
	private val itemPickupPattern = Pattern.compile("A (?<item>[A-z ]+) was picked up!")
	private val playerObtainPattern = Pattern.compile("(?:\\[[A-Z+]+] )?[A-z0-9_]+ has obtained (?<item>[A-z ]+)!")

	private val allowedItems = setOf("Wither Key", "Blood Key")
	private val deniedItems = setOf("Superboom TNT", "Revive Stone", "Premium Flesh", "Beating Heart", "Vitamin Death", "Optical Lens")

	override val enabled = SkyBlockIsland.DUNGEONS.inIsland() && config.pickupObtainMessage

	override fun shouldFilter(message: String): Boolean {
		itemPickupPattern.matchMatcher(message) {
			return config.allowKeyMessage && group("item") !in allowedItems
		}

		playerObtainPattern.matchMatcher(message) {
			val item = group("item")

			return when {
				item in allowedItems -> !config.allowKeyMessage
				item.startsWith("Blessing") || item in deniedItems -> true
				config.allow5050ItemMessage && item !in deniedItems -> false
				else -> false
			}
		}

		return false
	}
}