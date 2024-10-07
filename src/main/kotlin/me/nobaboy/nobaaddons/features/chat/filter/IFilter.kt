package me.nobaboy.nobaaddons.features.chat.filter

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filter.dungeon.BlessingFilter
import me.nobaboy.nobaaddons.features.chat.filter.dungeon.HealerOrbFilter
import me.nobaboy.nobaaddons.features.chat.filter.dungeon.PickupObtainFilter
import me.nobaboy.nobaaddons.features.chat.filter.general.ProfileInfoFilter
import me.nobaboy.nobaaddons.features.chat.filter.general.TipMessageFilter
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

interface IFilter {
	val config get() = NobaConfigManager.get().chat.filter

	fun isEnabled(): Boolean
	fun shouldFilter(message: Text, text: String): Boolean

	fun isEnabled(option: ChatFilterOption): Boolean = option != ChatFilterOption.SHOWN

	companion object {
		private var init = false
		private val filters = arrayOf<IFilter>(
			BlessingFilter,
			HealerOrbFilter,
			ProfileInfoFilter,
			TipMessageFilter,
			PickupObtainFilter,
		)

		fun init() {
			check(!init) { "Already initialized filters!" }
			init = true

			ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
				val text = message.string.clean()
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
