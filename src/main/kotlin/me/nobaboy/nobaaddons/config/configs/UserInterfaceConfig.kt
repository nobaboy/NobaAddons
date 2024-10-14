package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.config.controllers.infobox.InfoBox

class UserInterfaceConfig {
	@SerialEntry
	var showUsageText: Boolean = true

	@SerialEntry
	val infoBoxes: MutableList<InfoBox> = mutableListOf()
}