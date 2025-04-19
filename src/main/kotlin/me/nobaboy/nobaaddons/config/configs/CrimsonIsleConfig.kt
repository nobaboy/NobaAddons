package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.utils.enums.AnnounceChannel

class CrimsonIsleConfig {
	@Object val announceVanquisher = AnnounceVanquisher()

	class AnnounceVanquisher {
		var enabled = false
		var announceChannel = AnnounceChannel.PARTY
	}
}