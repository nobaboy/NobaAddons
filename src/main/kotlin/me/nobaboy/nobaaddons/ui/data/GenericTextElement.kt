package me.nobaboy.nobaaddons.ui.data

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.ui.TextShadow

/**
 * Generic implementation of an [TextElement], suitable for basic text elements that don't require
 * any extra properties.
 *
 * ## Example
 *
 * ```kt
 * object Config : Histoire(...) {
 *     @Object val element = TextElement(color = 0xFF00FF)
 * }
 * ```
 */
data class GenericTextElement(
	override var textShadow: TextShadow = TextShadow.SHADOW,
	override var color: Int = 0xFFFFFF,
	override var outlineColor: Int = 0x000000,
	@Object override val position: ElementPosition = ElementPosition(),
) : TextElement