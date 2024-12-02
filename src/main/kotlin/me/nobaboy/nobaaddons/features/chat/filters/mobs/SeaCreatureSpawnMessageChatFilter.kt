package me.nobaboy.nobaaddons.features.chat.filters.mobs

import me.nobaboy.nobaaddons.core.SeaCreature
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter

object SeaCreatureSpawnMessageChatFilter : IChatFilter {
	override fun isEnabled(): Boolean = config.hideSeaCreatureSpawnMessage

	override fun shouldFilter(message: String): Boolean {
		val seaCreature = SeaCreature.creatures[message] ?: return false

		return seaCreature.rarity.isAtMost(config.seaCreatureMaximumRarity)
	}
}