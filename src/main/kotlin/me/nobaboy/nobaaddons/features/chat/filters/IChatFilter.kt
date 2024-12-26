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
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

interface IChatFilter {
	val config get() = NobaConfigManager.config.chat.filters

	val enabled: Boolean
	fun shouldFilter(message: String): Boolean

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
				filters.asSequence().filter { it.enabled }.none {
					runCatching { it.shouldFilter(message.string.cleanFormatting()) }
						.onFailure { error ->
							ErrorManager.logError("${it::class.simpleName} threw an error while processing a chat message", error)
						}
						.getOrDefault(false)
				}
			}
		}
	}
}