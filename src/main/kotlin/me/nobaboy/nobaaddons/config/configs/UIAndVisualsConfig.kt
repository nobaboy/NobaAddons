package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.config.ui.controllers.InfoBox
import java.awt.Color

class UIAndVisualsConfig {
	@SerialEntry
	var showUsageText: Boolean = true

	@SerialEntry
	val etherwarpHelper: EtherwarpHelper = EtherwarpHelper()

	class EtherwarpHelper {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var overlayColor: Color = Color.BLUE

		@SerialEntry
		var showFailText: Boolean = false

		@SerialEntry
		var allowOnAir: Boolean = false
	}

	@SerialEntry
	val infoBoxes = mutableListOf<InfoBox>()
}