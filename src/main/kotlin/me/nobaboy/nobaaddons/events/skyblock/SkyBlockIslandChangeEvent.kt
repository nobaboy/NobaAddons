package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.events.internal.EventDispatcher

data class SkyBlockIslandChangeEvent(val island: IslandType) {
	companion object {
		val EVENT = EventDispatcher<SkyBlockIslandChangeEvent>()
	}
}