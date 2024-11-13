package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.data.PetData
import net.fabricmc.fabric.api.event.EventFactory

fun interface PetChangeEvent {
	fun onPetChange(oldPet: PetData?, newPet: PetData?)

	companion object {
		val EVENT = EventFactory.createArrayBacked(PetChangeEvent::class.java) { listeners ->
			PetChangeEvent { oldPet, newPet ->
				listeners.forEach { it.onPetChange(oldPet, newPet) }
			}
		}
	}
}