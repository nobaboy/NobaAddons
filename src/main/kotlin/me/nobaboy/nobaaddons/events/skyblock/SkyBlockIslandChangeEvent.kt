package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.utils.Scheduler
import net.fabricmc.fabric.api.event.EventFactory

fun interface SkyBlockIslandChangeEvent {
	fun onIslandChange(island: IslandType)

	companion object {
		val EVENT = EventFactory.createArrayBacked(SkyBlockIslandChangeEvent::class.java) { listeners ->
			SkyBlockIslandChangeEvent { location ->
				listeners.forEach { Scheduler.schedule(20) { it.onIslandChange(location) } }
			}
		}
	}
}