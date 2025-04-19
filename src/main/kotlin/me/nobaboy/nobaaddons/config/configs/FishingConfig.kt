package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.enums.AnnounceChannel
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class FishingConfig {
	var hideOtherPeopleFishing = false
	var hotspotWaypoints = false
	var catchTimerHudElement = false
	var fixFishHookFieldDesync = true

	@Object val seaCreatureAlert = SeaCreatureAlert()
	@Object val announceSeaCreatures = AnnounceSeaCreatures()
	@Object val bobberTimer = BobberTimer()
	@Object val catchMessages = CatchMessages()
	@Object val trophyFishing = TrophyFishing()
	@Object val highlightThunderSparks = HighlightThunderSparks()

	class SeaCreatureAlert {
		var enabled = false
		var nameInsteadOfRarity = false
		var minimumRarity = Rarity.LEGENDARY
		var notificationSound = NotificationSound.DING

		var carrotKing = false
		var nutcracker = false
	}

	class AnnounceSeaCreatures {
		var enabled = false
		var minimumRarity = Rarity.MYTHIC
		var announceChannel = AnnounceChannel.PARTY

		var carrotKing = false
		var nutcracker = false
	}

	class BobberTimer {
		var enabled = false
		var crimsonIsleOnly = true
	}

	// TODO: Add compact double hook catch message (again)
	class CatchMessages {
		var revertTreasureMessages = false
	}

	class TrophyFishing {
		var modifyChatMessages = false
		var compactMessages = false
		var compactMaxRarity = TrophyFishRarity.SILVER
	}

	class HighlightThunderSparks {
		var enabled = false
		var highlightColor = NobaColor(0x24DDE5)
		var showText = true
	}
}