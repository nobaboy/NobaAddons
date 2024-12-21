package me.nobaboy.nobaaddons.features.chat.filters.mobs

import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter

object SeaCreatureSpawnMessageChatFilter : IChatFilter {
	override val enabled: Boolean = config.hideSeaCreatureSpawnMessage

	override fun shouldFilter(message: String): Boolean {
		val seaCreature = SeaCreature.getBySpawnMessage(message) ?: return false

		return seaCreature.rarity.isAtMost(config.seaCreatureMaximumRarity)
	}
}