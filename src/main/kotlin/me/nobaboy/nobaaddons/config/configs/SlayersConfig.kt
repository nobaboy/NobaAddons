package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.features.slayers.BossTimeSource
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.serializers.ColorKSerializer
import java.awt.Color

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

		@Serializable(ColorKSerializer::class)
		var beaconPhaseColor = Color(255, 45, 156, 175)
		@Serializable(ColorKSerializer::class)
		var hitsPhaseColor = Color(255, 156, 70, 175)
		@Serializable(ColorKSerializer::class)
		var damagePhaseColor = Color(103, 136, 255, 175)

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