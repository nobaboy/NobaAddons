package me.nobaboy.nobaaddons.screens.infoboxes

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.screens.hud.elements.data.TextElement

object InfoBoxesConfig : AbstractConfig(NobaAddons.modConfigDir.resolve("info-boxes.json")) {
	val infoBoxes by Property.list("info_boxes", Serializer.expose<TextElement>())
}