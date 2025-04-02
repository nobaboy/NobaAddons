package me.nobaboy.nobaaddons.ui.data

import me.nobaboy.nobaaddons.ui.TextShadow

/**
 * Abstract text element configuration; extend this if you need to store extra information with
 * your element, or use [GenericTextElement] if you just need an instance for [me.nobaboy.nobaaddons.ui.TextHudElement].
 */
interface TextElement {
	val textShadow: TextShadow
	val color: Int
	val outlineColor: Int
	val position: ElementPosition
}