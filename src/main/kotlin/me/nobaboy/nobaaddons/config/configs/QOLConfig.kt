package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class QOLConfig {
	@SerialEntry
	val soundFilters: SoundFilters = SoundFilters()

	@SerialEntry
	val garden: Garden = Garden()

	class SoundFilters {
		@SerialEntry
		var muteWitherSkullAbilities: Boolean = false

		@SerialEntry
		var muteGoneWithTheWind: Boolean = false

		@SerialEntry
		var muteKillerSpring: Boolean = false

		@SerialEntry
		var muteReindrakeSpawn: Boolean = false

		@SerialEntry
		var muteReindrakeGiftDrop: Boolean = false
	}

	class Garden {
		@SerialEntry
		var reduceMouseSensitivity: Boolean = false

		@SerialEntry
		var reductionMultiplier: Int = 6

		@SerialEntry
		var isDaedalusFarmingTool: Boolean = false

		@SerialEntry
		var autoUnlockMouseOnTeleport: Boolean = false
	}
}