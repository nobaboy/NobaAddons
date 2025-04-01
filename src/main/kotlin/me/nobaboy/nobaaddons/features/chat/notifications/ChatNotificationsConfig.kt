package me.nobaboy.nobaaddons.features.chat.notifications

import dev.celestialfault.histoire.Histoire
import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import net.minecraft.util.Formatting

private val COLOR_REGEX = Regex("&([a-z0-9])", RegexOption.IGNORE_CASE)

object ChatNotificationsConfig : Histoire(NobaAddons.CONFIG_DIR.resolve("chat-notifications.json").toFile()) {
	var notifications: List<Notification> = mutableListOf()

	@Serializable
	data class Notification(
		var enabled: Boolean = true,
		var message: String = "",
		var display: String = "",
		var mode: NotificationMode = NotificationMode.CONTAINS,
	) {
		val colorFormattedDisplay: String get() = display.replace(COLOR_REGEX, "${Formatting.FORMATTING_CODE_PREFIX}$1")
	}
}