package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.filters.ability.AbilityChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.BlessingChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.HealerOrbChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.PickupObtainChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.ProfileInfoChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.TipMessagesChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.mobs.SeaCreatureSpawnMessageChatFilter
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

interface IChatFilter {
	val config get() = NobaConfigManager.config.chat.filters

	fun isEnabled(): Boolean
	fun shouldFilter(message: String): Boolean

	fun isEnabled(option: ChatFilterOption): Boolean = option != ChatFilterOption.SHOWN

	companion object {
		private var init = false
		private val filters = arrayOf<IChatFilter>(
			// Item Abilities
			AbilityChatFilter,
			// Mobs
			SeaCreatureSpawnMessageChatFilter,
			// Dungeons
			BlessingChatFilter,
			HealerOrbChatFilter,
			PickupObtainChatFilter,
			// Miscellaneous
			ProfileInfoChatFilter,
			TipMessagesChatFilter,
		)

		fun init() {
			check(!init) { "Already initialized chat filters!" }
			init = true

			ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
				filters.asSequence().filter { it.isEnabled() }.none {
					runCatching { it.shouldFilter(message.string.cleanFormatting()) }
						.onFailure { error ->
							NobaAddons.LOGGER.error("Filter {} threw an error while processing a chat message", it, error)
						}
						.getOrDefault(false)
				}
			}
		}
	}
}