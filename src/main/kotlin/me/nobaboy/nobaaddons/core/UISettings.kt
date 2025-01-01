package me.nobaboy.nobaaddons.core

import dev.celestialfault.celestialconfig.AbstractConfig
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.ui.data.TextElement

object UISettings : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("ui.json")) {
	val pickupLog by TextElement("pickupLog")
}