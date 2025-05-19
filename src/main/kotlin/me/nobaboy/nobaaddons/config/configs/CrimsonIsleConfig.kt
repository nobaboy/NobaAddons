package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.utils.enums.AnnounceChannel

class CrimsonIsleConfig {
	@Object val announceVanquisher = AnnounceVanquisher()
	@Object val minibossTimers = MinibossTimers()

	class AnnounceVanquisher {
		var enabled = false
		var announceChannel = AnnounceChannel.PARTY
	}

	class MinibossTimers {
		var enabled = false
	}
}