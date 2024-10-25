package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.config.ui.controllers.impl.InfoBox
import java.awt.Color

class UIAndVisualsConfig {
	@SerialEntry
	var showUsageText: Boolean = true

	@SerialEntry
	val temporaryWaypoints: TemporaryWaypoints = TemporaryWaypoints()

	@SerialEntry
	val etherwarpHelper: EtherwarpHelper = EtherwarpHelper()

	@SerialEntry
	val infoBoxes = mutableListOf<InfoBox>()

	@SerialEntry
	var renderingTweaks: RenderingTweaks = RenderingTweaks()

	class TemporaryWaypoints {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var waypointColor: Color = Color.YELLOW

		@SerialEntry
		var expirationTime: Int = 30
	}

	class EtherwarpHelper {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = Color.BLUE

		@SerialEntry
		var showFailText: Boolean = false

		@SerialEntry
		var allowOnAir: Boolean = false
	}

	class RenderingTweaks {
		@SerialEntry
		var hideOtherPeopleFishing: Boolean = false

		@SerialEntry
		var hideLightningBolt: Boolean = false
	}
}