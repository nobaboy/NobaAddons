package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class EventsConfig : ObjectProperty<EventsConfig>("events") {
	val hoppity by Hoppity()
	val mythological by Mythological()

	class Hoppity : ObjectProperty<Hoppity>("hoppity") {
		var requireMythicRabbit by Property.of<Boolean>("requireMythicRabbit", false)
	}

	class Mythological : ObjectProperty<Mythological>("mythological") {
		var burrowGuess by Property.of<Boolean>("burrowGuess", false)
		var findNearbyBurrows by Property.of<Boolean>("findNearbyBurrows", false)
		var dingOnBurrowFind by Property.of<Boolean>("dingOnBurrowFind", false)
		var removeGuessOnBurrowFind by Property.of<Boolean>("removeGuessOnBurrowFind", false)
		var findNearestWarp by Property.of<Boolean>("findNearestWarp", false)

		var alertInquisitor by Property.of<Boolean>("alertInquisitor", false)
		var alertOnlyInParty by Property.of<Boolean>("alertOnlyInParty", false)
		var notificationSound by Property.of("notificationSound", Serializer.enum(), NotificationSound.DING)
		var showInquisitorDespawnTime by Property.of<Boolean>("showInquisitorDespawnTime", false)
		var inquisitorFocusMode by Property.of<Boolean>("inquisitorFocusMode", false)

		var announceRareDrops by Property.of<Boolean>("announceRareDrops", false)

		var ignoreCrypt by Property.of<Boolean>("ignoreCrypt", false)
		var ignoreWizard by Property.of<Boolean>("ignoreWizard", false)
		var ignoreStonks by Property.of<Boolean>("ignoreStonks", false)
	}
}