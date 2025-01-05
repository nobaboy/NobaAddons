package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.features.slayers.BossTimeSource
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color

class SlayersConfig {
	@SerialEntry
	val bossAlert: BossAlert = BossAlert()

	@SerialEntry
	val miniBossAlert: MiniBossAlert = MiniBossAlert()

	@SerialEntry
	val highlightMiniBosses: HighlightMiniBosses = HighlightMiniBosses()

	@SerialEntry
	val announceBossKillTime: AnnounceBossKillTime = AnnounceBossKillTime()

	@SerialEntry
	val voidgloom: Voidgloom = Voidgloom()

	class AnnounceBossKillTime {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var timeSource: BossTimeSource = BossTimeSource.REAL_TIME
	}

	class BossAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var alertText: String = "Boss Spawned!"

		@SerialEntry
		var alertColor: Color = NobaColor.RED.toColor()
	}

	class MiniBossAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var alertText: String = "MiniBoss Spawned!"

		@SerialEntry
		var alertColor: Color = NobaColor.RED.toColor()
	}

	class HighlightMiniBosses {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = NobaColor.BLUE.toColor()
	}

	class Voidgloom {
		@SerialEntry
		var brokenHeartRadiationTimer: Boolean = false

		@SerialEntry
		var yangGlyphAlert: Boolean = false

		@SerialEntry
		var alertColor: Color = NobaColor.RED.toColor()

		@SerialEntry
		var highlightYangGlyph: Boolean = false

		@SerialEntry
		var highlightNukekubi: Boolean = false

		@SerialEntry
		var highlightColor: Color = NobaColor.RED.toColor()
	}
}