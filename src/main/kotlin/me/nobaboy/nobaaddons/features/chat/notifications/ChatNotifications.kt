package me.nobaboy.nobaaddons.features.chat.notifications

import me.nobaboy.nobaaddons.config.NobaConfigUtils.safeLoad
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import net.minecraft.util.Util
import java.util.regex.PatternSyntaxException
import kotlin.time.Duration.Companion.seconds

object ChatNotifications {
	private val GROUP = Regex("(?<!\\\\)\\\$(\\d+)")

	private val regex = Util.memoize<String, Regex?> {
		try {
			Regex(it)
		} catch(e: PatternSyntaxException) {
			ErrorManager.logError("Invalid chat notification regex", e)
			null
		}
	}

	fun init() {
		ChatNotificationsConfig.safeLoad()
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onChatMessage(message: String) {
		ChatNotificationsConfig.notifications.filter { it.enabled }.forEach {
			if(it.message.isBlank() || it.display.isBlank()) return@forEach
			var display: String? = null

			when(it.mode) {
				NotificationMode.CONTAINS -> if(!message.contains(it.message)) return@forEach
				NotificationMode.STARTS_WITH -> if(!message.startsWith(it.message)) return@forEach
				NotificationMode.REGEX -> {
					val regex = this.regex.apply(it.message) ?: return@forEach
					val match = regex.find(message) ?: return@forEach
					display = it.colorFormattedDisplay
					display = display.replace(GROUP) {
						val group = it.groups[1]?.value?.toInt() ?: return@replace it.value
						match.groups[group]?.value ?: it.value
					}
				}
			}

			if(display == null) display = it.colorFormattedDisplay
			SoundUtils.dingHighSound.play()
			RenderUtils.drawTitle(display, NobaColor.WHITE, 2.75f, duration = 2.seconds, id = "chat_notification")
			return
		}
	}
}