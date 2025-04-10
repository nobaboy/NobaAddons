package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.config.util.ProfileData
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
	 * Event invoked when a [me.nobaboy.nobaaddons.config.util.ProfileDataLoader] detects a profile switch and loads
	 * the new profile's data
	 *
	 * Note that this event is *only* invoked when a profile switch is detected, and is not invoked when
	 * profile data is loaded through other means (e.g. [me.nobaboy.nobaaddons.config.util.ProfileDataLoader.getOrPut])
	 */
	val PROFILE_DATA_LOADED = EventDispatcher<ProfileDataLoaded<*>>()

	data class IslandChange(val island: SkyBlockIsland) : Event
	data class PetChange(val oldPet: PetData?, val newPet: PetData?) : Event
	data class ProfileChange(val profileId: UUID) : Event
	data class ProfileDataLoaded<T : ProfileData>(val profileId: UUID, val data: T) : Event
}