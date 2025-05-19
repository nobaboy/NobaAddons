package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.features.chat.filters.ability.AbilityChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.BlessingChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.HealerOrbChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.PickupObtainChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.AutoPetChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.ProfileInfoChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.TipMessagesChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.mobs.SeaCreatureCatchMessageChatFilter
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.owdding.ktmodules.Module
import net.minecraft.text.Text

interface IChatFilter {
	val config get() = NobaConfig.chat.filters

	val enabled: Boolean

	fun shouldFilter(message: Text): Boolean = shouldFilter(message.string.cleanFormatting())
	fun shouldFilter(message: String): Boolean

	@Module
	companion object {
		private val filters = arrayOf(
			// Item Abilities
			AbilityChatFilter,
			// Mobs
			SeaCreatureCatchMessageChatFilter,
			// Dungeons
			BlessingChatFilter,
			HealerOrbChatFilter,
			PickupObtainChatFilter,
			// Miscellaneous
			AutoPetChatFilter,
			ProfileInfoChatFilter,
			TipMessagesChatFilter,
		)

		init {
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
			}
		}
	}
}