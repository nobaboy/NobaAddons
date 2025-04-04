package me.nobaboy.nobaaddons.features.ui.infobox

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.ui.TextShadow
import me.nobaboy.nobaaddons.ui.data.ElementPosition
import me.nobaboy.nobaaddons.ui.data.TextElement

@Serializable
data class InfoBoxElement(
	override var textShadow: TextShadow = TextShadow.SHADOW,
	override var color: Int = 0xFFFFFF,
	override var outlineColor: Int = 0x000000,
	override val position: ElementPosition = ElementPosition(),
	var text: String = "",
) : TextElement