package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.filters.ability.AbilityFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.BlessingFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.HealerOrbFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.PickupObtainFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.ProfileInfoFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.TipMessageFilter
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

interface IFilter {
	val config get() = NobaConfigManager.get().chat.filters

	fun isEnabled(): Boolean
	fun shouldFilter(message: Text, text: String): Boolean

	fun isEnabled(option: ChatFilterOption): Boolean = option != ChatFilterOption.SHOWN

	companion object {
		private var init = false
		private val filters = arrayOf<IFilter>(
			AbilityFilter,
			// Dungeons
			BlessingFilter,
			HealerOrbFilter,
			PickupObtainFilter,
			// Miscellaneous
			ProfileInfoFilter,
			TipMessageFilter,
		)

		fun init() {
			check(!init) { "Already initialized filters!" }
			init = true

			ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
				val text = message.string.cleanFormatting()
				filters.asSequence().filter { it.isEnabled() }.none {
					runCatching { it.shouldFilter(message, text) }
						.onFailure { error ->
							NobaAddons.LOGGER.error("Filter {} threw an error while processing a chat message", it, error)
						}
						.getOrDefault(false)
				}
			}
		}
	}
}