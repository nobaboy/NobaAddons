package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.ChatMessageEvents
import me.nobaboy.nobaaddons.features.chat.filters.ability.AbilityChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.BlessingChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.HealerOrbChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.PickupObtainChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.ProfileInfoChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.TipMessagesChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.mobs.SeaCreatureSpawnMessageChatFilter
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.minecraft.text.Text

interface IChatFilter {
	val config get() = NobaConfig.INSTANCE.chat.filters

	val enabled: Boolean

	fun shouldFilter(message: Text): Boolean = shouldFilter(message.string.cleanFormatting())
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

			ChatMessageEvents.ALLOW.register { event ->
				for(filter in filters.asSequence()) {
					if(!filter.enabled) continue
					try {
						if(filter.shouldFilter(event.message)) {
							event.cancel()
							break
						}
					} catch(ex: Throwable) {
						ErrorManager.logError("${filter::class.simpleName} threw an error while processing a chat message", ex)
					}
				}
//				}
			}
		}
	}
}