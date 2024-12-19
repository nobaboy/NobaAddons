package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.core.MobRarity
import me.nobaboy.nobaaddons.utils.sound.NotificationSound
import java.awt.Color

class FishingConfig {
	@SerialEntry
	val bobberTimer: BobberTimer = BobberTimer()

	@SerialEntry
	val trophyFishing: TrophyFishing = TrophyFishing()

	@SerialEntry
	val seaCreatureAlert: SeaCreatureAlert = SeaCreatureAlert()

	@SerialEntry
	val highlightThunderSparks: HighlightThunderSparks = HighlightThunderSparks()

	class TrophyFishing {
		@SerialEntry
		var modifyChatMessages: Boolean = false
	}

	class BobberTimer {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var crimsonIsleOnly: Boolean = true

		@SerialEntry
		var lerpColor: Boolean = false
	}

	class SeaCreatureAlert {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var nameInsteadOfRarity: Boolean = false

		@SerialEntry
		var minimumRarity: MobRarity = MobRarity.LEGENDARY

		@SerialEntry
		var carrotKingIsRare: Boolean = false

		@SerialEntry
		var announceInPartyChat: Boolean = false

		@SerialEntry
		var notificationSound: NotificationSound = NotificationSound.DING
	}

	class HighlightThunderSparks {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = Color(0x24DDE5)

		@SerialEntry
		var showText: Boolean = false
	}
}