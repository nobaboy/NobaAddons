package me.nobaboy.nobaaddons.features.keybinds

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.migrations.Migrations
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.features.keybinds.impl.KeyBind

private val migrations = Migrations("configVersion") {
	add { it["keyBinds"] = it.remove("keybinds") ?: return@add }
}

object KeyBindsConfig : Histoire(NobaAddons.CONFIG_DIR.resolve("keybinds.json").toFile(), migrations = migrations) {
	var keyBinds: MutableList<KeyBind> = mutableListOf()

	// this cannot be private in any capacity (not even a `private set`), as reflection totally shits the bed
	// and behaves incredibly inconsistently when encountering a private var in an object class
	@Suppress("unused")
	var configVersion: Int = migrations.currentVersion
}