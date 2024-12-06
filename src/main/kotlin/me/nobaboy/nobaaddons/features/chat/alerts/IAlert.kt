package me.nobaboy.nobaaddons.features.chat.alerts

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.MythicSeaCreatureAlert
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.VanquisherAlert
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

interface IAlert {
	val config get() = NobaConfigManager.config.chat.alerts

	val enabled: Boolean
	fun shouldAlert(message: Text, text: String): Boolean

	companion object {
		private var init = false
		private var alerts = arrayOf<IAlert>(
			MythicSeaCreatureAlert,
			VanquisherAlert
		)

		fun init() {
			check(!init) { "Already initialized alerts!" }
			init = true

			ClientReceiveMessageEvents.GAME.register { message, _ ->
				val text = message.string.cleanFormatting()
				alerts.asSequence().filter { it.enabled }.forEach {
					runCatching { it.shouldAlert(message, text) }
						.onFailure { error ->
							NobaAddons.LOGGER.error(
								"Alert {} threw an error while processing a chat message", it, error
							)
						}
				}
			}
		}
	}
}