package me.nobaboy.nobaaddons.ui.data

import me.nobaboy.nobaaddons.ui.TextShadow

/**
 * Basic text element configuration element; use [GenericTextElement] if you just need an instance for
 * [me.nobaboy.nobaaddons.ui.TextHudElement], and don't need to do anything special with it.
 */
interface TextElement {
	val textShadow: TextShadow
	val color: Int
	val outlineColor: Int
	val position: ElementPosition
}