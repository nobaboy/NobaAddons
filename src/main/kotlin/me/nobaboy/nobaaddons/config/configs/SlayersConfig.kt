package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.features.slayers.BossTimeSource
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.awtColor
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.color
import java.awt.Color

class SlayersConfig : ObjectProperty<SlayersConfig>("slayers") {
	val bossAlert by BossAlert()
	val miniBossAlert by MiniBossAlert()
	val highlightMiniBosses by HighlightMiniBosses()
	val announceBossKillTime by AnnounceBossKillTime()
	val sven by Sven()
	val voidgloom by Voidgloom()
	val inferno by Inferno()

	class BossAlert : ObjectProperty<BossAlert>("bossAlert") {
		var enabled by Property.of<Boolean>("enabled", false)
		var alertColor by Property.of("alertColor", Serializer.color, NobaColor.RED)
	}

	class MiniBossAlert : ObjectProperty<MiniBossAlert>("miniBossAlert") {
		var enabled by Property.of<Boolean>("enabled", false)
		var alertColor by Property.of("alertColor", Serializer.color, NobaColor.RED)
	}

	class HighlightMiniBosses : ObjectProperty<HighlightMiniBosses>("highlightMiniBosses") {
		var enabled by Property.of<Boolean>("enabled", false)
		var highlightColor by Property.of("highlightColor", Serializer.color, NobaColor.GOLD)
	}

	class AnnounceBossKillTime : ObjectProperty<AnnounceBossKillTime>("announceBossKillTime") {
		var enabled by Property.of<Boolean>("enabled", false)
		var timeSource by Property.of("timeSource", Serializer.enum(), BossTimeSource.REAL_TIME)
	}

	class Sven : ObjectProperty<Sven>("sven") {
		var hidePupNametags by Property.of<Boolean>("hidePupNametags", false)
	}

	class Voidgloom : ObjectProperty<Voidgloom>("voidgloom") {
		var highlightPhases by Property.of<Boolean>("highlightPhases", false)
		var beaconPhaseColor by Property.of("beaconPhaseColor", Serializer.awtColor, Color(255, 45, 156, 175))
		var hitsPhaseColor by Property.of("hitsPhaseColor", Serializer.awtColor, Color(255, 156, 70, 175))
		var damagePhaseColor by Property.of("damagePhaseColor", Serializer.awtColor, Color(103, 136, 255, 175))

		var yangGlyphAlert by Property.of<Boolean>("yangGlyphAlert", false)
		var yangGlyphAlertColor by Property.of("yangGlyphAlertColor", Serializer.color, NobaColor.RED)
		var highlightYangGlyphs by Property.of<Boolean>("highlightYangGlyphs", false)
		var yangGlyphHighlightColor by Property.of("yangGlyphHighlightColor", Serializer.color, NobaColor.RED)

		var highlightNukekubiFixations by Property.of<Boolean>("highlightNukekubiFixations", false)
		var nukekubiFixationHighlightColor by Property.of("nukekubiFixationHighlightColor", Serializer.color, NobaColor.RED)

		var brokenHeartRadiationTimer by Property.of<Boolean>("brokenHeartRadiationTimer", false)
	}

	class Inferno : ObjectProperty<Inferno>("inferno") {
		var highlightHellionShield by Property.of<Boolean>("highlightHellionShield", false)
	}
}