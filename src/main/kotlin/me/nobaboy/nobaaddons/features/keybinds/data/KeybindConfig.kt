package me.nobaboy.nobaaddons.features.keybinds.data

import com.google.gson.JsonObject
import me.celestialfault.celestialconfig.AbstractConfig
import me.celestialfault.celestialconfig.Property
import me.celestialfault.celestialconfig.Serializer
import me.celestialfault.celestialconfig.properties.ObjectProperty
import me.nobaboy.nobaaddons.NobaAddons
import org.lwjgl.glfw.GLFW

object KeybindConfig : AbstractConfig(NobaAddons.modDir.resolve("nobaaddons").resolve("keybinds.json")) {
	val keybinds by Property.list("keybinds", Serializer.obj<Keybind>())

	class Keybind() : ObjectProperty<Keybind>("") {
		@Suppress("unused") constructor(obj: JsonObject) : this() {
			load(obj)
		}

		var command by Property.string("command", default = "").notNullable()
		var keycode by Property.int("key", default = GLFW.GLFW_KEY_UNKNOWN).notNullable()
	}
}