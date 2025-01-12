package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.color
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class FishingConfig : ObjectProperty<FishingConfig>("fishing") {
	val seaCreatureAlert by SeaCreatureAlert()
	val bobberTimer by BobberTimer()
	val trophyFishing by TrophyFishing()
	val highlightThunderSparks by HighlightThunderSparks()

	class SeaCreatureAlert : ObjectProperty<SeaCreatureAlert>("seaCreatureAlert") {
		var enabled by Property.of<Boolean>("enabled", false)
		var nameInsteadOfRarity by Property.of<Boolean>("nameInsteadOfRarity", false)
		var minimumRarity by Property.of("minimumRarity", Serializer.enum(), Rarity.LEGENDARY)
		var carrotKingIsRare by Property.of<Boolean>("carrotKingIsRare", false)
		var announceInPartyChat by Property.of<Boolean>("announceInPartyChat", false)
		var notificationSound by Property.of("notificationSound", Serializer.enum(), NotificationSound.DING)
	}

	class BobberTimer : ObjectProperty<BobberTimer>("bobberTimer") {
		var enabled by Property.of<Boolean>("enabled", false)
		var crimsonIsleOnly by Property.of<Boolean>("crimsonIsleOnly", true)
	}

	class TrophyFishing() : ObjectProperty<TrophyFishing>("trophyFishing") {
		var modifyChatMessages by Property.of<Boolean>("modifyChatMessages", false)
	}

	class HighlightThunderSparks : ObjectProperty<HighlightThunderSparks>("highlightThunderSparks") {
		var enabled by Property.of<Boolean>("enabled", false)
		var highlightColor by Property.of("highlightColor", Serializer.color, NobaColor(0x24DDE5))
		var showText by Property.of<Boolean>("showText", false)
	}
}