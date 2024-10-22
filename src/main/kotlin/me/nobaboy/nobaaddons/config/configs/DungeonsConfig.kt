package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class DungeonsConfig {
	@SerialEntry
	val simonSaysTimer: SimonSaysTimerConfig = SimonSaysTimerConfig()

	class SimonSaysTimerConfig {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var timeInPartyChat: Boolean = false
	}
}