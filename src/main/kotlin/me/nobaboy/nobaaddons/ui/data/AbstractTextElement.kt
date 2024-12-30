package me.nobaboy.nobaaddons.ui.data

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.ui.TextShadow

/**
 * Abstract text element configuration; extend this if you need to store extra information with
 * your element, or use [TextElement] if you just need an instance for [me.nobaboy.nobaaddons.ui.TextHudElement].
 */
abstract class AbstractTextElement<T> protected constructor(key: String = "") : ObjectProperty<T>(key) {
	var textShadow by Property.of("textMode", Serializer.enum<TextShadow>(), TextShadow.SHADOW)
	var color by Property.of("color", 0xFFFFFF)
	var outlineColor by Property.of("outlineColor", 0x000000)
	val position by ElementPosition("position")
}