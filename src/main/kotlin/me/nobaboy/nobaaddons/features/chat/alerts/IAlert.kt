package me.nobaboy.nobaaddons.features.chat.alerts

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.MythicSeaCreatureAlert
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.VanquisherAlert
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

interface IAlert {
	val config get() = NobaConfigManager.config.chat.alerts

	val enabled: Boolean
	fun shouldAlert(message: String): Boolean

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
				alerts.asSequence().filter { it.enabled }.forEach {
					runCatching { it.shouldAlert(message.string.cleanFormatting()) }
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