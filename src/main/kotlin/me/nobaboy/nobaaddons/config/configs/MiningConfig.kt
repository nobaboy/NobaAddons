package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class MiningConfig {
	@Object val wormAlert = WormAlert()
	@Object val glaciteMineshaft = GlaciteMineshaft()

	class WormAlert {
		var enabled = false
		var alertColor = NobaColor.RED
		var notificationSound = NotificationSound.DING
	}

	class GlaciteMineshaft {
		var corpseLocator = false
		var autoShareCorpses = false
		var entranceWaypoint = false
		var ladderWaypoint = false
	}
}