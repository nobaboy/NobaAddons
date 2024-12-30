package me.nobaboy.nobaaddons.ui.elements.data

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.ui.elements.TextMode

abstract class AbstractTextElement<T> protected constructor(key: String = "") : ObjectProperty<T>(key) {
	var textMode by Property.of("textMode", Serializer.enum<TextMode>(), TextMode.SHADOW)
	var color by Property.of("color", 0xFFFFFF)
	var outlineColor by Property.of("outlineColor", 0x000000)
	val position by ElementPosition("position")
}