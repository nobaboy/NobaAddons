package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class MiningConfig {
	@SerialEntry
	val glaciteMineshaft: GlaciteMineshaft = GlaciteMineshaft()

	class GlaciteMineshaft {
		@SerialEntry
		var corpseLocator: Boolean = false

		@SerialEntry
		var autoShareCorpseCoords: Boolean = false

		@SerialEntry
		var entranceWaypoint: Boolean = false

		@SerialEntry
		var ladderWaypoint: Boolean = false
	}
}