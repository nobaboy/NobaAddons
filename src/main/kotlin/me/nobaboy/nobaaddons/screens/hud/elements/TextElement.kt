package me.nobaboy.nobaaddons.screens.hud.elements

data class TextElement(
	val text: String,
	val mode: TextMode = TextMode.SHADOW,
	val element: Element
)