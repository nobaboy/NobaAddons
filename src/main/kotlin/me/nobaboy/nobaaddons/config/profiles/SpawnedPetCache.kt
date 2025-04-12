package me.nobaboy.nobaaddons.config.profiles

import me.nobaboy.nobaaddons.config.util.ProfileDataLoader
import me.nobaboy.nobaaddons.config.util.ProfileData
import me.nobaboy.nobaaddons.data.PetData
import java.util.UUID

class SpawnedPetCache private constructor(profile: UUID?) : ProfileData(profile, "current_pet.json") {
	var pet: PetData? = null

	companion object {
		// loader needs to be exposed here to allow for migrating from legacy data
		internal val loader = ProfileDataLoader<SpawnedPetCache>(::SpawnedPetCache)
		val PROFILE by loader
	}
}
