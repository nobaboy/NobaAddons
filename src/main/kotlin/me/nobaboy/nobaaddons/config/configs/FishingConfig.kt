package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.core.MobRarity
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class FishingConfig {
	@SerialEntry
	val seaCreatureAlert: SeaCreatureAlert = SeaCreatureAlert()

	@SerialEntry
	var showBobberTimer: Boolean = false

	@SerialEntry
	var lerpBobberTimer: Boolean = false

	class SeaCreatureAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var nameInsteadOfRarity: Boolean = false

		@SerialEntry
		var minimumRarity: MobRarity = MobRarity.LEGENDARY

		@SerialEntry
		var notificationSound: NotificationSound = NotificationSound.DING
	}
}