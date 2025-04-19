package me.nobaboy.nobaaddons.features.chat.filters.mobs

import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter

object SeaCreatureCatchMessageChatFilter : IChatFilter {
	override val enabled: Boolean get() = config.hideSeaCreatureCatchMessage

	override fun shouldFilter(message: String): Boolean =
		SeaCreature.getBySpawnMessage(message)?.let { it.rarity <= config.seaCreatureMaxRarity } == true
}