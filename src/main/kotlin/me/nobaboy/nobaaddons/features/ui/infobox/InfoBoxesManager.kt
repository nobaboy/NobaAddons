package me.nobaboy.nobaaddons.features.ui.infobox

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.utils.ErrorManager

object InfoBoxesManager {
	val infoBoxes by InfoBoxesConfig::infoBoxes

	fun init() {
		try {
			InfoBoxesConfig.load()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to load info boxes", ex)
		}
	}

	fun save() {
		try {
			InfoBoxesConfig.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save info boxes", ex)
		}
	}

	fun create(): InfoBoxElement = InfoBoxElement().apply {
		position.x = 100
		position.y = 100
	}
}