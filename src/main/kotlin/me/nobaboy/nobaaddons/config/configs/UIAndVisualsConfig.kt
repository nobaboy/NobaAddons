package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.config.ui.elements.TextElement
import me.nobaboy.nobaaddons.core.MobRarity
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color

class UIAndVisualsConfig {
	@SerialEntry
	var showUsageText: Boolean = true

	@SerialEntry
	val temporaryWaypoints: TemporaryWaypoints = TemporaryWaypoints()

	@SerialEntry
	val etherwarpHelper: EtherwarpHelper = EtherwarpHelper()

	@SerialEntry
	val seaCreatureAlert: SeaCreatureAlert = SeaCreatureAlert()

	@SerialEntry
	val renderingTweaks: RenderingTweaks = RenderingTweaks()

	@SerialEntry
	val infoBoxes = mutableListOf<TextElement>()

	class TemporaryWaypoints {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var waypointColor: Color = NobaColor.YELLOW.toColor()

		@SerialEntry
		var expirationTime: Int = 15
	}

	class EtherwarpHelper {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = NobaColor.BLUE.toColor()

		@SerialEntry
		var showFailText: Boolean = false

		@SerialEntry
		var allowOnAir: Boolean = false
	}

	class SeaCreatureAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var nameInsteadOfRarity: Boolean = false

		@SerialEntry
		var minimumRarity: MobRarity = MobRarity.LEGENDARY
	}

	class RenderingTweaks {
		@SerialEntry
		var hideOtherPeopleFishing: Boolean = false

		@SerialEntry
		var hideLightningBolt: Boolean = false
	}
}