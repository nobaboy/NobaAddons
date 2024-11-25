package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class QOLConfig {
	@SerialEntry
	val soundFilters: SoundFilters = SoundFilters()

	class SoundFilters {
		@SerialEntry
		var muteWitherSkullAbilities: Boolean = false

		@SerialEntry
		var muteGoneWithTheWind: Boolean = false

		@SerialEntry
		var muteKillerSpring: Boolean = false
	}
}