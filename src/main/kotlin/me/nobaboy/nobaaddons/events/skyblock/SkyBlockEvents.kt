package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents.IslandChange

object SkyBlockEvents {
	val ISLAND_CHANGE = EventDispatcher<IslandChange>()

	val PET_CHANGE = EventDispatcher<PetChange>()

	data class IslandChange(val island: SkyBlockIsland)
	data class PetChange(val oldPet: PetData?, val newPet: PetData?)
}