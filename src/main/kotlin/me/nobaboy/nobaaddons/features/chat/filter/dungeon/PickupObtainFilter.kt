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

	private val itemPickupPattern: Pattern = Pattern.compile("A (?<item>[A-z ]+) was picked up!")
	private val playerObtainPattern: Pattern =
		Pattern.compile("(?:\\[[A-Z+]+] )?[A-z0-9_]+ has obtained (?<item>[A-z ]+)!")

	private val ignoredItems =
		listOf("Superboom TNT", "Revive Stone", "Premium Flesh", "Beating Heart", "Vitamin Death", "Optical Lens")
	private val allowedItems = listOf("Wither Key", "Blood Key")

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
			return@register processMessage(message.string.clean())
		}
	}

	private fun processMessage(message: String): Boolean {
		if(!isEnabled()) return true

		itemPickupPattern.matchMatcher(message) {
			if(config.pickupObtainMessage) return group("item") !in allowedItems
		}

		playerObtainPattern.matchMatcher(message) {
			val item = group("item")

			if(config.allow5050ItemMessage) {
				if(item in ignoredItems && item !in allowedItems) return false
			} else {
				if(item in ignoredItems) return false
			}
		}

		return true
	}

	private fun isEnabled(): Boolean {
		return IslandType.DUNGEONS.inIsland() && config.pickupObtainMessage
	}
}