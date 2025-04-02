package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class FishingConfig {
	var hideOtherPeopleFishing = false
	var catchTimerHudElement = false

	@Object val seaCreatureAlert = SeaCreatureAlert()
	@Object val bobberTimer = BobberTimer()
	@Object val trophyFishing = TrophyFishing()
	@Object val catchMessages = CatchMessages()
	@Object val highlightThunderSparks = HighlightThunderSparks()

	class SeaCreatureAlert {
		var enabled = false
		var nameInsteadOfRarity = false
		var minimumRarity = Rarity.LEGENDARY
		var carrotKingIsRare = false
		var announceInPartyChat = false
		var notificationSound = NotificationSound.DING
	}

	class BobberTimer {
		var enabled = false
		var crimsonIsleOnly = true
	}

	class TrophyFishing {
		var modifyChatMessages = false
		var compactMessages = false
		var compactMaxRarity = TrophyFishRarity.SILVER
	}

	class CatchMessages {
		var revertTreasureMessages = false
		// TODO add a custom sea creature catch message formatting, like what skyhanni has
	}

	class HighlightThunderSparks {
		var enabled = false
		var highlightColor = NobaColor(0x24DDE5)
		var showText = true
	}
}