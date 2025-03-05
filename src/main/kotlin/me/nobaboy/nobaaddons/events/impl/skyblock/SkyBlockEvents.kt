package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.profile.ProfileData
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.AbstractEvent
import me.nobaboy.nobaaddons.events.EventDispatcher
import java.util.UUID

object SkyBlockEvents {
	@Deprecated("Use the companion object instead")
	val ISLAND_CHANGE = EventDispatcher<IslandChange>()

	val PET_CHANGE = EventDispatcher<PetChange>()

	val PROFILE_CHANGE = EventDispatcher<ProfileChange>()
	val PROFILE_DATA_LOADED = EventDispatcher<ProfileDataLoad>()

	data class IslandChange(val island: SkyBlockIsland) : AbstractEvent() {
		companion object : EventDispatcher<IslandChange>()
	}

	data class PetChange(val oldPet: PetData?, val newPet: PetData?) : AbstractEvent()
	data class ProfileChange(val profileId: UUID) : AbstractEvent()
	data class ProfileDataLoad(val profileId: UUID, val data: ProfileData) : AbstractEvent()
}