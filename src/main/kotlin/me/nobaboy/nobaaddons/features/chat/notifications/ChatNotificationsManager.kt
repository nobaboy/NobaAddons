package me.nobaboy.nobaaddons.features.chat.notifications

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.config.util.safeLoad
import me.nobaboy.nobaaddons.utils.ErrorManager

object ChatNotificationsManager {
	val notifications get() = ChatNotificationsConfig.notifications

	fun init() {
		ChatNotificationsConfig.safeLoad()
	}

	fun save() {
		try {
			ChatNotificationsConfig.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save chat notifications", ex)
		}
	}
}