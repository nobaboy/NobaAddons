package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class EventsConfig {
	@SerialEntry
	var mythological: Mythological = Mythological()

	class Mythological {
		@SerialEntry
		var burrowGuess: Boolean = false

		@SerialEntry
		var burrowTriangulate: Boolean = false

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