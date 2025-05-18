package me.nobaboy.nobaaddons.features.ui.infobox

import dev.celestialfault.histoire.Histoire
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.annotations.ConfigModule

@ConfigModule
object InfoBoxesConfig : Histoire(NobaAddons.CONFIG_DIR.resolve("infoboxes.json").toFile(), migrations = migrations) {
	var infoBoxes: MutableList<InfoBoxElement> = mutableListOf()

	// this cannot be private in any capacity (not even a `private set`), as reflection totally shits the bed
	// and behaves incredibly inconsistently when encountering a private var in an object class
	@Suppress("unused")
	var configVersion: Int = migrations.currentVersion

	override fun load() {
		super.load()
		InfoBoxesManager.recreateUIElements()
	}
}