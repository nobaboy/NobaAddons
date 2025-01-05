package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.sound.NotificationSound
import java.awt.Color

class MiningConfig {
	@SerialEntry
	val wormAlert: WormAlert = WormAlert()

	@SerialEntry
	val glaciteMineshaft: GlaciteMineshaft = GlaciteMineshaft()

	class WormAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var alertColor: Color = NobaColor.RED.toColor()

		@SerialEntry
		var notificationSound: NotificationSound = NotificationSound.DING
	}

	class GlaciteMineshaft {
		@SerialEntry
		var corpseLocator: Boolean = false

		@SerialEntry
		var autoShareCorpseCoords: Boolean = false

		@SerialEntry
		var entranceWaypoint: Boolean = false

		@SerialEntry
		var ladderWaypoint: Boolean = false
	}
}