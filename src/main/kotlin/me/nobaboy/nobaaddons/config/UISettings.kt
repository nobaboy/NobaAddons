package me.nobaboy.nobaaddons.config

import dev.celestialfault.celestialconfig.AbstractConfig
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigUtils.saveOnExit
import me.nobaboy.nobaaddons.ui.data.TextElement

object UISettings : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("ui.json")) {
	init {
		saveOnExit()
	}

	val itemPickupLog by TextElement("itemPickupLog")
	val mythologicalTracker by TextElement("mythologicalTracker")
}