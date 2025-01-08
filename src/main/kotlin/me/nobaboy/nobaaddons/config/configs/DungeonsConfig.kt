package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.color
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.HighlightMode

class DungeonsConfig : ObjectProperty<DungeonsConfig>("dungeons") {
	val simonSaysTimer by SimonSaysTimerConfig()
	val highlightStarredMobs by HighlightStarredMobs()

	class SimonSaysTimerConfig : ObjectProperty<SimonSaysTimerConfig>("simonSaysTimer") {
		var enabled by Property.of<Boolean>("enabled", false)
		var timeInPartyChat by Property.of<Boolean>("timeInPartyChat", false)
	}

	class HighlightStarredMobs : ObjectProperty<HighlightStarredMobs>("highlightStarredMobs") {
		var enabled by Property.of<Boolean>("enabled", false)
		var highlightColor by Property.of("highlightColor", Serializer.color, NobaColor.YELLOW)
		var highlightMode by Property.of("highlightMode", Serializer.enum(), HighlightMode.FILLED_OUTLINE)
	}
}