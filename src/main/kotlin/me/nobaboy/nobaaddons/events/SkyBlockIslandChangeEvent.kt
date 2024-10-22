package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.api.data.IslandType
import net.fabricmc.fabric.api.event.EventFactory

fun interface SkyBlockIslandChangeEvent {
	fun onIslandChange(island: IslandType)

	companion object {
		val EVENT = EventFactory.createArrayBacked(SkyBlockIslandChangeEvent::class.java) { listeners ->
			SkyBlockIslandChangeEvent { location ->
				listeners.forEach { it.onIslandChange(location) }
			}
		}
	}
}