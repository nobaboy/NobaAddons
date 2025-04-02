package me.nobaboy.nobaaddons.features.keybinds

import dev.celestialfault.histoire.Histoire
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.features.keybinds.impl.KeyBind

object KeyBindsConfig : Histoire(NobaAddons.CONFIG_DIR.resolve("keybinds.json").toFile()) {
	val keyBinds: MutableList<KeyBind> = mutableListOf()
}