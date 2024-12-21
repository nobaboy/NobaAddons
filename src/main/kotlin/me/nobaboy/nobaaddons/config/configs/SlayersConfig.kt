package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.features.slayers.BossTimeSource
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color

class SlayersConfig {
	@SerialEntry
	val announceBossKillTime: AnnounceBossKillTime = AnnounceBossKillTime()

	@SerialEntry
	val miniBossAlert: MiniBossAlert = MiniBossAlert()

	@SerialEntry
	val highlightMiniBosses: HighlightMiniBosses = HighlightMiniBosses()

	class AnnounceBossKillTime {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var timeSource: BossTimeSource = BossTimeSource.REAL_TIME
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
}