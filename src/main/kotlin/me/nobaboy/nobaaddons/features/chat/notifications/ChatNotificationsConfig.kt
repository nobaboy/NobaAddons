package me.nobaboy.nobaaddons.features.chat.notifications

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property.Companion.of as prop
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons

object ChatNotificationsConfig : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("notifications.json")) {
	val notifications by prop("notifications", Serializer.list(Serializer.obj<Notification>()), mutableListOf())

	class Notification() : ObjectProperty<Notification>("") {
		constructor(json: JsonObject) : this() {
			load(json)
		}

		var message: String by prop("message", "")
		var displayMessage: String by prop("display", "")
		var regex: Boolean by prop("regex", true)

		fun copy() = Notification(save() as JsonObject)
	}
}