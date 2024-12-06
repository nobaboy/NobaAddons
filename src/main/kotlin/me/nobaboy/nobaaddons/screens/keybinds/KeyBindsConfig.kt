package me.nobaboy.nobaaddons.screens.keybinds

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.screens.keybinds.impl.KeyBind

object KeyBindsConfig : AbstractConfig(NobaAddons.modConfigDir.resolve("key-binds.json")) {
	val keyBinds by Property.list("keyBinds", Serializer.expose<KeyBind>())
}