package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class EventsConfig {
	@SerialEntry
	var mythological: Mythological = Mythological()

	class Mythological {
		@SerialEntry
		var burrowGuess: Boolean = false

		@SerialEntry
		var findNearbyBurrows: Boolean = false

		@SerialEntry
		var removeGuessOnBurrowFind: Boolean = false

		@SerialEntry
		var findNearestWarp: Boolean = false

		@SerialEntry
		var alertInquisitor: Boolean = false

		@SerialEntry
		var alertOnlyInParty: Boolean = false

		@SerialEntry
		var zeldaSecretSoundOnInquisitor: Boolean = false

		@SerialEntry
		var showInquisitorDespawnTime: Boolean = false

		@SerialEntry
		var inquisitorFocusMode: Boolean = false

		@SerialEntry
		var announceRareDrops: Boolean = false

		@SerialEntry
		var ignoreCrypt: Boolean = false

		@SerialEntry
		var ignoreWizard: Boolean = false

		@SerialEntry
		var ignoreStonks: Boolean = false
	}
}