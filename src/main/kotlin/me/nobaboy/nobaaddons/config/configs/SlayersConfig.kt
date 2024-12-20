package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.sound.NotificationSound
import java.awt.Color

class SlayersConfig {
	@SerialEntry
	val miniBoss: MiniBoss = MiniBoss()

	class MiniBoss {
		@SerialEntry
		var alert: Boolean = false

		@SerialEntry
		var alertText: String = "MiniBoss Spawned!"

		@SerialEntry
		var notificationSound: NotificationSound = NotificationSound.DING

		@SerialEntry
		var alertColor: Color = NobaColor.RED.toColor()

		@SerialEntry
		var highlight: Boolean = false

		@SerialEntry
		var highlightColor: Color = NobaColor.BLUE.toColor()
	}
}