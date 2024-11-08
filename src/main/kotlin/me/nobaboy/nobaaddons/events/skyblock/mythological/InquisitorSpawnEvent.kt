package me.nobaboy.nobaaddons.events.skyblock.mythological

import net.fabricmc.fabric.api.event.EventFactory

fun interface InquisitorSpawnEvent {
	fun onInquisitorSpawn()

	companion object {
		val EVENT = EventFactory.createArrayBacked(InquisitorSpawnEvent::class.java) { listeners ->
			InquisitorSpawnEvent {
				listeners.forEach { it.onInquisitorSpawn() }
			}
		}
	}
}