package me.nobaboy.nobaaddons.features.chat.filter.dungeon

import me.nobaboy.nobaaddons.api.SkyblockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Pattern

object PickupObtainFilter {
	private val config get() = NobaConfigManager.get().chat.filter

	private val itemPickupPattern = Pattern.compile("A (?<item>[A-z ]+) was picked up!")
	private val playerObtainPattern = Pattern.compile("(?:\\[[A-Z+]+] )?[A-z0-9_]+ has obtained (?<item>[A-z ]+)!")

	private val allowedItems = listOf("Wither Key", "Blood Key")
	private val deniedItems = listOf(
		"Superboom TNT", "Revive Stone", "Premium Flesh", "Beating Heart", "Vitamin Death", "Optical Lens"
	)

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ -> processMessage(message.string.clean()) }
	}

	private fun processMessage(message: String): Boolean {
		if(!isEnabled()) return true

		itemPickupPattern.matchMatcher(message) {
			if(config.pickupObtainMessage) return group("item") !in allowedItems
		}

		playerObtainPattern.matchMatcher(message) {
			val item = group("item")

			val allow5050Items = config.allow5050ItemMessage
			return when {
				allow5050Items && !item.startsWith("Blessing") && item !in deniedItems && item !in allowedItems -> true
				!allow5050Items && (item.startsWith("Blessing") || item in deniedItems) -> false
				else -> true
			}
		}

		return true
	}

	private fun isEnabled() = IslandType.DUNGEONS.inIsland() && config.pickupObtainMessage
}