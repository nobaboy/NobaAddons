package me.nobaboy.nobaaddons.config

import dev.celestialfault.celestialconfig.AbstractConfig
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.saveOnExit
import me.nobaboy.nobaaddons.ui.data.ElementPosition
import me.nobaboy.nobaaddons.ui.data.TextElement

object UISettings : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("ui.json")) {
	init {
		saveOnExit()
	}

	val itemPickupLog by TextElement("itemPickupLog")
	val catchTimer by TextElement("catchTimer", position = ElementPosition(x = 0.485, y = 0.55, scale = 2f))
}