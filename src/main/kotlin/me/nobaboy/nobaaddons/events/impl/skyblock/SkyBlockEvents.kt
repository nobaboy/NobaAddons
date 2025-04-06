package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import java.util.UUID

object SkyBlockEvents {
	/**
	 * Event invoked when switching servers
	 */
	val ISLAND_CHANGE = EventDispatcher<IslandChange>()

	/**
	 * Event invoked when the player switches pets, either through the pets menu or autopet
	 */
	val PET_CHANGE = EventDispatcher<PetChange>()

	/**
	 * Event invoked when a profile switch is detected
	 */
	val PROFILE_CHANGE = EventDispatcher<ProfileChange>()

	/**
	 * Event invoked when *any* profile data is loaded by a [me.nobaboy.nobaaddons.config.util.PerProfileDataLoader]
	 */
	val PROFILE_DATA_LOADED = EventDispatcher<ProfileDataLoad>()

	data class IslandChange(val island: SkyBlockIsland) : Event()
	data class PetChange(val oldPet: PetData?, val newPet: PetData?) : Event()
	data class ProfileChange(val profileId: UUID) : Event()
	data class ProfileDataLoad(val profileId: UUID, val data: Any) : Event()
}