package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.features.slayers.BossTimeSource
import me.nobaboy.nobaaddons.utils.NobaColor

class SlayersConfig {
	@Object val bossAlert = BossAlert()
	@Object val miniBossAlert = MiniBossAlert()
	@Object val highlightMiniBosses = HighlightMiniBosses()

	@Object val bossKillTime = BossKillTime()
	@Object val compactMessages = CompactMessages()

	@Object val sven = Sven()
	@Object val voidgloom = Voidgloom()
	@Object val inferno = Inferno()

	class BossAlert {
		var enabled = false
		var alertColor = NobaColor.RED
	}

	class MiniBossAlert {
		var enabled = false
		var alertColor = NobaColor.RED
	}

	class HighlightMiniBosses {
		var enabled = false
		var highlightColor = NobaColor.GOLD
	}

	class BossKillTime {
		var enabled = false
		var timeSource = BossTimeSource.REAL_TIME
	}

	class CompactMessages {
		var enabled = false
		var removeLastMessage = false
	}

	class Sven {
		var hidePupNametags = false
	}

	class Voidgloom {
		var highlightPhases = false

		var beaconPhaseColor = NobaColor(0xff2d9c)
		var hitsPhaseColor = NobaColor(0xff9c46)
		var damagePhaseColor = NobaColor(0x6788ff)

		var yangGlyphAlert = false
		var yangGlyphAlertColor = NobaColor.RED
		var highlightYangGlyphs = false
		var yangGlyphHighlightColor = NobaColor.RED

		var highlightNukekubiFixations = false
		var nukekubiFixationHighlightColor = NobaColor.RED

		var brokenHeartRadiationTimer = false
	}

	class Inferno {
		var highlightHellionShield = false
	}
}