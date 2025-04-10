package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher

object FishingEvents {
	val SEA_CREATURE_CATCH = EventDispatcher<SeaCreatureCatch>()

	data class SeaCreatureCatch(val seaCreature: SeaCreature, val doubleHook: Boolean) : Event
}