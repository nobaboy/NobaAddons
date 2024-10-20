package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import java.awt.Color

class CrimsonIsleConfig {
	@SerialEntry
	val highlightThunderSparks: HighlightThunderSparks = HighlightThunderSparks()

	class HighlightThunderSparks {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = Color(0x44AEDA)

		@SerialEntry
		var showText: Boolean = false
	}
}