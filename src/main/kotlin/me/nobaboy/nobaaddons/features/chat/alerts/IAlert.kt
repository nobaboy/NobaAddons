package me.nobaboy.nobaaddons.features.chat.alerts

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.ChatMessageEvents
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.MythicSeaCreatureAlert
import me.nobaboy.nobaaddons.features.chat.alerts.crimsonisle.VanquisherAlert
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting

interface IAlert {
	val config get() = NobaConfig.INSTANCE.chat.alerts

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

			ChatMessageEvents.CHAT.register { (message) ->
				alerts.asSequence().filter { it.enabled }.forEach {
					runCatching { it.shouldAlert(message.string.cleanFormatting()) }
						.onFailure { error ->
							ErrorManager.logError(
								"${it::class.simpleName} threw an error while processing a chat message", error
							)
						}
				}
			}
		}
	}
}