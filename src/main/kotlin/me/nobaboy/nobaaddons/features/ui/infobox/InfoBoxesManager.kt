package me.nobaboy.nobaaddons.features.ui.infobox

import dev.celestialfault.celestialconfig.migrations.ConfigTooNewException
import kotlinx.io.IOException
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.ErrorManager

object InfoBoxesManager {
	val infoBoxes by InfoBoxesConfig::infoBoxes

	fun init() {
		try {
			InfoBoxesConfig.load()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to load info boxes", ex)
		} catch(ex: ConfigTooNewException) {
			ErrorManager.logError("Failed to load info boxes", ex)
		}
		recreateUIElements()
	}

	fun save() {
		try {
			InfoBoxesConfig.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save info boxes", ex)
		}
	}

	fun recreateUIElements() {
		UIManager.removeIf { it is InfoBoxHud }
		infoBoxes.forEach { UIManager.add(InfoBoxHud(it))}
	}
}