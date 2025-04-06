package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object

class QOLConfig {
	@Object val soundFilters = SoundFilters()
	@Object val garden = Garden()

	class SoundFilters {
		var muteWitherSkullAbilities = false

		var muteBanshee = false
		var muteReindrakeSpawn = false
		var muteReindrakeGiftDrop = false

		var muteGoneWithTheWind = false

		var muteKillerSpring = false

		var mutePunch = false
	}

	class Garden {
		var reduceMouseSensitivity = false
		var reductionMultiplier = 6
		var autoUnlockMouseOnTeleport = false
	}
}