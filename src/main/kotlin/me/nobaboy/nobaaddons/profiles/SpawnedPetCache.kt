package me.nobaboy.nobaaddons.profiles

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.config.util.PerProfileDataLoader
import me.nobaboy.nobaaddons.data.PetData

@Serializable
data class SpawnedPetCache(var pet: PetData? = null) {
	companion object {
		// loader needs to be exposed here to allow for migrating from legacy data
		internal val loader = PerProfileDataLoader<SpawnedPetCache>("current_pet.json")
		val PROFILE by loader
	}
}
