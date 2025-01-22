package me.nobaboy.nobaaddons.core.hoppity

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.NobaVec

@Serializable
data class HoppityEgg(val eggLocations: Map<SkyBlockIsland, List<NobaVec>>) {
	companion object {
		val LOCATIONS by Repo.create("data/hoppity.json", serializer())

		fun getByIsland(island: SkyBlockIsland) = LOCATIONS?.eggLocations[island]
	}
}