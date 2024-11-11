package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import java.util.regex.Pattern

object PetAPI {
	private val petsMenuPattern = Pattern.compile("Pets(?: \\(\\d+/\\d+\\) )?")

	var currentPet: PetData? = null
		private set

	fun init() {
		InventoryEvents.READY.register { handleInventoryReady(it) }
	}

	private fun handleInventoryReady(inventory: InventoryData) {
		if(!petsMenuPattern.matches(inventory.title)) return


	}
}