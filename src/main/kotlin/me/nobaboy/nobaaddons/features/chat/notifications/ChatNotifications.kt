package me.nobaboy.nobaaddons.features.chat.notifications

import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import java.io.IOException
import java.util.regex.PatternSyntaxException

object ChatNotifications {
	private val DING = SoundEvents.ENTITY_ARROW_HIT_PLAYER
	private val COLOR_REGEX = Regex("&([a-z0-9])", RegexOption.IGNORE_CASE)
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
		ClientReceiveMessageEvents.GAME.register { message, overlay ->
			if(overlay) return@register
			onChatMessage(message.string.cleanFormatting())
		}
		ClientReceiveMessageEvents.GAME_CANCELED.register { message, overlay ->
			if(overlay) return@register
			onChatMessage(message.string.cleanFormatting())
		}
		try {
			ChatNotificationsConfig.load()
		} catch(e: IOException) {
			ErrorManager.logError("Failed to load chat notifications", e)
		}
	}

	private fun onChatMessage(message: String) {
		ChatNotificationsConfig.notifications.forEach {
			if(it.message.isBlank() || it.displayMessage.isBlank()) return@forEach
			val regex = this.regex.apply(it.message) ?: return@forEach

			var match: MatchResult? = null
			if(it.regex) {
				match = regex.find(message) ?: return@forEach
			} else {
				if(it.message !in message) return@forEach
			}

			var display = it.displayMessage.replace(COLOR_REGEX, "${Formatting.FORMATTING_CODE_PREFIX}$1")

			if(it.regex) {
				display = display.replace(GROUP) {
					val group = it.groups[1]?.value?.toInt() ?: return@replace it.value
					match!!.groups[group]?.value ?: return@replace it.value
				}
			}

			SoundUtils.playSound(DING)
			RenderUtils.drawTitle(display, NobaColor.WHITE, scale = 2.75f)
			return
		}
	}
}