package me.nobaboy.nobaaddons.config.ui.controllers.impl

import me.nobaboy.nobaaddons.config.ui.elements.Element
import me.nobaboy.nobaaddons.config.ui.elements.TextElement

data class InfoBox(
	val text: String,
	val mode: TextElement.TextMode = TextElement.TextMode.SHADOW,
	val element: Element
)