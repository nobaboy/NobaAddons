package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.internal.EventDispatcher

data class PetChangeEvent(val oldPet: PetData?, val newPet: PetData?) {
	companion object {
		val EVENT = EventDispatcher<PetChangeEvent>()
	}
}