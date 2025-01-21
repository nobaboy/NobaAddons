package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property

class QOLConfig : ObjectProperty<QOLConfig>("qol") {
	val soundFilters by SoundFilters()
	val garden by Garden()

	class SoundFilters : ObjectProperty<SoundFilters>("soundFilters") {
		var muteWitherSkullAbilities by Property.of<Boolean>("muteWitherSkullAbilities", false)

		var muteReindrakeSpawn by Property.of<Boolean>("muteReindrakeSpawn", false)
		var muteReindrakeGiftDrop by Property.of<Boolean>("muteReindrakeGiftDrop", false)

		var muteGoneWithTheWind by Property.of<Boolean>("muteGoneWithTheWind", false)

		var muteKillerSpring by Property.of<Boolean>("muteKillerSpring", false)

		var mutePunch by Property.of<Boolean>("mutePunch", false)
	}

	class Garden : ObjectProperty<Garden>("garden") {
		var reduceMouseSensitivity by Property.of<Boolean>("reduceMouseSensitivity", false)
		var reductionMultiplier by Property.of<Int>("reductionMultiplier", 6)
		var autoUnlockMouseOnTeleport by Property.of<Boolean>("autoUnlockMouseOnTeleport", false)
	}
}