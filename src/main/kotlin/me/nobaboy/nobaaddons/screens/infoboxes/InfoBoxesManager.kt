package me.nobaboy.nobaaddons.screens.infoboxes

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.screens.hud.elements.TextMode
import me.nobaboy.nobaaddons.screens.hud.elements.data.Element
import me.nobaboy.nobaaddons.screens.hud.elements.data.TextElement

object InfoBoxesManager {
	val infoBoxes = mutableListOf<TextElement>()

	init {
		runCatching {
			InfoBoxesConfig.load()
			infoBoxes.clear()
			infoBoxes.addAll(InfoBoxesConfig.infoBoxes)
		}.onFailure {
			NobaAddons.LOGGER.error("Failed to load info-boxes.json", it)
		}
	}

	fun saveInfoBoxes() {
		try {
			InfoBoxesConfig.infoBoxes.clear()
			InfoBoxesConfig.infoBoxes.addAll(infoBoxes)
			InfoBoxesConfig.save()
		} catch(ex: IOException) {
			NobaAddons.LOGGER.error("Failed to save info-boxes.json", ex)
		}
	}

	fun getNewInfoBox(infoBoxes: List<TextElement>): TextElement {
		val identifier = "Info Box ${infoBoxes.size + 1}"
		return TextElement(
			text = "",
			textMode = TextMode.SHADOW,
			outlineColor = 0x000000,
			element = Element(identifier, 100, 100)
		)
	}
}