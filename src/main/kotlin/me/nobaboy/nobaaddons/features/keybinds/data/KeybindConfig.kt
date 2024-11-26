package me.nobaboy.nobaaddons.features.keybinds.data

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import dev.celestialfault.celestialconfig.properties.ObjectProperty
import me.nobaboy.nobaaddons.NobaAddons
import org.lwjgl.glfw.GLFW

object KeybindConfig : AbstractConfig(NobaAddons.modConfigDir.resolve("keybinds.json")) {
	val keybinds by Property.list("keybinds", Serializer.obj<Keybind>())

	class Keybind() : ObjectProperty<Keybind>("") {
		@Suppress("unused") constructor(obj: JsonObject) : this() {
			load(obj)
		}

		var command by Property.string("command", default = "").notNullable()
		var keycode by Property.int("key", default = GLFW.GLFW_KEY_UNKNOWN).notNullable()
	}
}