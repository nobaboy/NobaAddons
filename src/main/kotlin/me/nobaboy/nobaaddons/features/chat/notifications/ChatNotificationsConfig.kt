package me.nobaboy.nobaaddons.features.chat.notifications

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons

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

		fun copy() = Notification(save() as JsonObject)
	}
}