package me.nobaboy.nobaaddons.core.events

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.NobaVec

@Serializable
data class HoppityData(val eggLocations: Map<SkyBlockIsland, List<NobaVec>>) {
	companion object {
		val HOPPITY by Repo.create("data/hoppity.json", serializer())

		fun getEggsByIsland(island: SkyBlockIsland) = HOPPITY?.eggLocations[island]
	}
}