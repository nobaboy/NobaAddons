package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color

class SlayersConfig {
	@SerialEntry
	val highlightMiniBosses: HighlightMiniBosses = HighlightMiniBosses()

	@SerialEntry
	val miniBossAlert: MiniBossAlert = MiniBossAlert()

	class HighlightMiniBosses {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = NobaColor.BLUE.toColor()
	}

	class MiniBossAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var alertText: String = "MiniBoss Spawned!"

		@SerialEntry
		var alertColor: Color = NobaColor.RED.toColor()
	}
}