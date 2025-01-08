package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.features.slayers.BossTimeSource
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.color

class SlayersConfig : ObjectProperty<SlayersConfig>("slayers") {
	val bossAlert by BossAlert()
	val miniBossAlert by MiniBossAlert()
	val announceBossKillTime by AnnounceBossKillTime()
	val voidgloom by Voidgloom()

	class BossAlert : ObjectProperty<BossAlert>("bossAlert") {
		var enabled by Property.of<Boolean>("enabled", false)
		var alertColor by Property.of("alertColor", Serializer.color, NobaColor.RED)
	}

	class MiniBossAlert : ObjectProperty<MiniBossAlert>("miniBossAlert") {
		var enabled by Property.of<Boolean>("enabled", false)
		var alertColor by Property.of("alertColor", Serializer.color, NobaColor.RED)
	}

	class AnnounceBossKillTime : ObjectProperty<AnnounceBossKillTime>("announceBossKillTime") {
		var enabled by Property.of<Boolean>("enabled", false)
		var timeSource by Property.of("timeSource", Serializer.enum(), BossTimeSource.REAL_TIME)
	}

	class Voidgloom : ObjectProperty<Voidgloom>("voidgloom"){
		var brokenHeartRadiationTimer by Property.of<Boolean>("brokenHeartRadiationTimer", false)

		var yangGlyphAlert by Property.of<Boolean>("yangGlyphAlert", false)
		var yangGlyphAlertColor by Property.of("yangGlyphAlertColor", Serializer.color, NobaColor.RED)
		var highlightYangGlyphs by Property.of<Boolean>("highlightYangGlyphs", false)
		var yangGlyphHighlightColor by Property.of("yangGlyphHighlightColor", Serializer.color, NobaColor.RED)

		var highlightNukekubiFixations by Property.of<Boolean>("highlightNukekubiFixations", false)
		var nukekubiFixationHighlightColor by Property.of("nukekubiFixationHighlightColor", Serializer.color, NobaColor.RED)
	}
}