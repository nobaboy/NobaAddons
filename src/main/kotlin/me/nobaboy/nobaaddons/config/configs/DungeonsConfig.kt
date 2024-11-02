package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.HighlightMode
import java.awt.Color

class DungeonsConfig {
	@SerialEntry
	val simonSaysTimer: SimonSaysTimerConfig = SimonSaysTimerConfig()

	@SerialEntry
	val highlightStarredMobs: HighlightStarredMobs = HighlightStarredMobs()

	class SimonSaysTimerConfig {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var timeInPartyChat: Boolean = false
	}

	class HighlightStarredMobs {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = NobaColor.YELLOW.toColor()

		@SerialEntry
		var highlightMode: HighlightMode = HighlightMode.FILLED_OUTLINE
	}
}