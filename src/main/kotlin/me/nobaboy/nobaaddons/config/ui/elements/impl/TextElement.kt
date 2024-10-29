package me.nobaboy.nobaaddons.config.ui.elements.impl

import me.nobaboy.nobaaddons.config.ui.elements.Element
import me.nobaboy.nobaaddons.config.ui.elements.TextMode

data class TextElement(
	val text: String,
	val mode: TextMode = TextMode.SHADOW,
	val element: Element
)