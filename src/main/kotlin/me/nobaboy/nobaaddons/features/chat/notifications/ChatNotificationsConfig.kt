package me.nobaboy.nobaaddons.features.chat.notifications

import dev.celestialfault.histoire.Histoire
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.annotations.ConfigModule
import net.minecraft.util.Formatting

@ConfigModule
object ChatNotificationsConfig : Histoire(NobaAddons.CONFIG_DIR.resolve("chat-notifications.json").toFile()) {
	var notifications: MutableList<Notification> = mutableListOf()

	@Serializable
	data class Notification(
		var enabled: Boolean = true,
		var message: String = "",
		@SerialName("notification")
		var display: String = "",
		var mode: NotificationMode = NotificationMode.CONTAINS,
	) {
		val colorFormattedDisplay: String get() = display.replace(CommonPatterns.COLOR_REGEX, "${Formatting.FORMATTING_CODE_PREFIX}$1")
	}
}