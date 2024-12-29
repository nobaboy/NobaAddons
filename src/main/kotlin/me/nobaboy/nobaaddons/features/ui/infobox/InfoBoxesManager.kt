package me.nobaboy.nobaaddons.features.ui.infobox

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.ui.elements.data.Element
import me.nobaboy.nobaaddons.ui.elements.data.TextElement
import me.nobaboy.nobaaddons.utils.ErrorManager

object InfoBoxesManager {
	val infoBoxes = mutableListOf<TextElement>()

	fun init() {
		try {
			InfoBoxesConfig.load()
			infoBoxes.addAll(InfoBoxesConfig.infoBoxes)
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to load info boxes", ex)
		}
	}

	fun saveInfoBoxes() {
		try {
			InfoBoxesConfig.infoBoxes.clear()
			InfoBoxesConfig.infoBoxes.addAll(infoBoxes)
			InfoBoxesConfig.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save info boxes", ex)
		}
	}

	fun getNewInfoBox(number: Int): TextElement {
		val identifier = "Info Box $number"
		return TextElement(element = Element(identifier, 100, 100))
	}
}