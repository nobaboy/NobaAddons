package me.nobaboy.nobaaddons.features.ui.infobox

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.config.util.safeLoad
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.ErrorManager

object InfoBoxesManager {
	val infoBoxes by InfoBoxesConfig::infoBoxes

	fun save() {
		try {
			InfoBoxesConfig.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save info boxes", ex)
		}
	}

	fun recreateUIElements() {
		UIManager.removeIf { it is InfoBoxHudElement }
		infoBoxes.forEach { UIManager.add(InfoBoxHudElement(it)) }
	}
}