package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.features.fishing.SeaCreatureType
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.color
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class FishingConfig : ObjectProperty<FishingConfig>("fishing") {
	var hideOtherPeopleFishing by Property.of<Boolean>("hideOtherPeopleFishing", false)

	val bobberTimer by BobberTimer()
	val seaCreatureAlert by SeaCreatureAlert()
	val trophyFishing by TrophyFishing()
	val highlightThunderSparks by HighlightThunderSparks()
	val announceSeaCreatures by AnnounceSeaCreatures()

	class BobberTimer : ObjectProperty<BobberTimer>("bobberTimer") {
		var enabled by Property.of<Boolean>("enabled", false)
		var crimsonIsleOnly by Property.of<Boolean>("crimsonIsleOnly", true)
	}

	class SeaCreatureAlert : ObjectProperty<SeaCreatureAlert>("seaCreatureAlert") {
		var enabled by Property.of<Boolean>("enabled", false)
		var nameInsteadOfRarity by Property.of<Boolean>("nameInsteadOfRarity", false)
		var minimumRarity by Property.of("minimumRarity", Serializer.enum(), Rarity.LEGENDARY)
		var carrotKingIsRare by Property.of<Boolean>("carrotKingIsRare", false)
		var announceInPartyChat by Property.of<Boolean>("announceInPartyChat", false)
		var notificationSound by Property.of("notificationSound", Serializer.enum(), NotificationSound.DING)
	}

	class AnnounceSeaCreatures : ObjectProperty<AnnounceSeaCreatures>("announceSeaCreatures") {
		var enabled by Property.of<Boolean>("enabled", false)
		var seaCreatureType by Property.of("seaCreatureType", Serializer.enum(), SeaCreatureType.RARITY)
		var minimumRarity by Property.of("minimumRarity", Serializer.enum(), Rarity.MYTHIC)
		var onlyInPartyChat by Property.of<Boolean>("onlyInPartyChat", false)
	}

	class TrophyFishing : ObjectProperty<TrophyFishing>("trophyFishing") {
		var modifyChatMessages by Property.of<Boolean>("modifyChatMessages", false)
		var compactMessages by Property.of<Boolean>("compactMessages", false)
		var compactMaxRarity by Property.of<TrophyFishRarity>("compactMaxRarity", Serializer.enum(), TrophyFishRarity.SILVER)
	}

	class HighlightThunderSparks : ObjectProperty<HighlightThunderSparks>("highlightThunderSparks") {
		var enabled by Property.of<Boolean>("enabled", false)
		var highlightColor by Property.of("highlightColor", Serializer.color, NobaColor(0x24DDE5))
		var showText by Property.of<Boolean>("showText", false)
	}
}