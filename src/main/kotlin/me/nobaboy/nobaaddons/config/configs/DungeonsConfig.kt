package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.HighlightMode

class DungeonsConfig {
	@Object val highlightStarredMobs = HighlightStarredMobs()
	@Object val simonSaysTimer = SimonSaysTimerConfig()

	class HighlightStarredMobs {
		var enabled = false
		var highlightColor = NobaColor.YELLOW
		var highlightMode = HighlightMode.FILLED_OUTLINE
	}

	class SimonSaysTimerConfig {
		var enabled = false
		var timeInPartyChat = false
	}
}