package me.nobaboy.nobaaddons.ui.elements.data

import com.google.gson.annotations.Expose
import me.nobaboy.nobaaddons.ui.elements.TextMode

data class TextElement(
	@Expose var text: String = "",
	@Expose var textMode: TextMode = TextMode.SHADOW,
	@Expose var outlineColor: Int = 0x000000,
	@Expose val element: Element
)