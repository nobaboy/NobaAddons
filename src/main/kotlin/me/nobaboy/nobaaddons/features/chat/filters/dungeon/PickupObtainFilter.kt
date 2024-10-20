package me.nobaboy.nobaaddons.features.chat.filters.dungeon

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.features.chat.filters.IFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import net.minecraft.text.Text
import java.util.regex.Pattern

object PickupObtainFilter : IFilter {
	private val itemPickupPattern = Pattern.compile("A (?<item>[A-z ]+) was picked up!")
	private val playerObtainPattern = Pattern.compile("(?:\\[[A-Z+]+] )?[A-z0-9_]+ has obtained (?<item>[A-z ]+)!")

	private val allowedItems = listOf("Wither Key", "Blood Key")
	private val deniedItems = listOf(
		"Superboom TNT", "Revive Stone", "Premium Flesh", "Beating Heart", "Vitamin Death", "Optical Lens"
	)

	override fun shouldFilter(message: Text, text: String): Boolean {
		itemPickupPattern.matchMatcher(text) {
			if(config.pickupObtainMessage) return group("item") in allowedItems
		}

		playerObtainPattern.matchMatcher(text) {
			val item = group("item")

			val allow5050Items = config.allow5050ItemMessage
			return when {
				allow5050Items && !item.startsWith("Blessing") && item !in deniedItems && item !in allowedItems -> false
				!allow5050Items && (item.startsWith("Blessing") || item in deniedItems) -> true
				else -> false
			}
		}

		return false
	}

	override fun isEnabled() = IslandType.DUNGEONS.inIsland() && config.pickupObtainMessage
}