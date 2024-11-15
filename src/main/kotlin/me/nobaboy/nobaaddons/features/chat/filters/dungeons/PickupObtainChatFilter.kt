package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import net.minecraft.text.Text
import java.util.regex.Pattern

object PickupObtainChatFilter : IChatFilter {
	private val itemPickupPattern = Pattern.compile("A (?<item>[A-z ]+) was picked up!")
	private val playerObtainPattern = Pattern.compile("(?:\\[[A-Z+]+] )?[A-z0-9_]+ has obtained (?<item>[A-z ]+)!")

	private val allowedItems = setOf("Wither Key", "Blood Key")
	private val deniedItems = setOf("Superboom TNT", "Revive Stone", "Premium Flesh", "Beating Heart", "Vitamin Death", "Optical Lens")

	override fun shouldFilter(message: Text, text: String): Boolean {
		itemPickupPattern.matchMatcher(text) {
			return config.allowKeyMessage && group("item") !in allowedItems
		}

		playerObtainPattern.matchMatcher(text) {
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

	override fun isEnabled() = IslandType.DUNGEONS.inIsland() && config.pickupObtainMessage
}