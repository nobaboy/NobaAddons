package me.nobaboy.nobaaddons.config.ui.elements

data class TextElement(
	val text: String,
	val mode: TextMode = TextMode.SHADOW,
	val element: Element
)