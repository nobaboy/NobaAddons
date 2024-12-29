package me.nobaboy.nobaaddons.features.ui.infobox

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.ui.elements.data.TextElement

object InfoBoxesConfig : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("infoboxes.json")) {
	val infoBoxes by Property.Companion.of("infoboxes", Serializer.Companion.list(Serializer.Companion.expose<TextElement>()), mutableListOf())
}