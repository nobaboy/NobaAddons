package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.core.MobRarity

class FishingConfig {
	@SerialEntry
	val seaCreatureAlert: SeaCreatureAlert = SeaCreatureAlert()

	class SeaCreatureAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var nameInsteadOfRarity: Boolean = false

		@SerialEntry
		var minimumRarity: MobRarity = MobRarity.LEGENDARY
	}
}