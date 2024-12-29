package me.nobaboy.nobaaddons.features.chat.notifications

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import net.minecraft.util.Formatting

private val COLOR_REGEX = Regex("&([a-z0-9])", RegexOption.IGNORE_CASE)

object ChatNotificationsConfig : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("chat-notifications.json")) {
	val notifications by Property.of("notifications", Serializer.list(Serializer.obj<Notification>()), mutableListOf())

	class Notification() : ObjectProperty<Notification>("") {
		constructor(json: JsonObject) : this() {
			load(json)
		}

		var enabled: Boolean by Property.of("enabled", true)
		var message: String by Property.of("message", "")
		var display: String by Property.of("notification", "")
		var mode: NotificationMode by Property.of("mode", Serializer.enum<NotificationMode>(), NotificationMode.CONTAINS)

		val colorFormattedDisplay: String get() = display.replace(COLOR_REGEX, "${Formatting.FORMATTING_CODE_PREFIX}$1")

		fun copy() = Notification(save() as JsonObject)
	}
}