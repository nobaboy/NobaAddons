package me.nobaboy.nobaaddons.config

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.saveOnExit
import me.nobaboy.nobaaddons.ui.data.ElementPosition
import me.nobaboy.nobaaddons.ui.data.GenericTextElement

object UISettings : Histoire(NobaAddons.CONFIG_DIR.resolve("ui.json").toFile()) {
	init {
		saveOnExit()
	}

	@Object val itemPickupLog = GenericTextElement()
	@Object val catchTimer = GenericTextElement(position = ElementPosition(x = 0.48, y = 0.55, scale = 2f))
	@Object val mythologicalTracker = GenericTextElement()
}