package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.utils.enums.AnnounceChannel
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class EventsConfig {
	@Object val hoppity = Hoppity()
	@Object val mythological = Mythological()

	class Hoppity {
		var eggGuess = false
		var requireMythicRabbit = false
	}

	class Mythological {
		var burrowGuess = false
		var findNearbyBurrows = false
		var dingOnBurrowFind = false
		var removeGuessOnBurrowFind = false
		var findNearestWarp = false

		var alertInquisitor = false
		var announceChannel = AnnounceChannel.PARTY
		var notificationSound = NotificationSound.DING
		var showInquisitorDespawnTime = false
		var inquisitorFocusMode = false

		var announceRareDrops = false

		var ignoreCrypt = false
		var ignoreWizard = false
		var ignoreStonks = false
	}
}