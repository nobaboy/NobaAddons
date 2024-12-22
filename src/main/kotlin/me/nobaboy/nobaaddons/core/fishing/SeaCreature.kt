package me.nobaboy.nobaaddons.core.fishing

import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.repo.RepoObjectArray.Companion.listFromRepository
import me.nobaboy.nobaaddons.utils.StringUtils.title

data class SeaCreature(
	val id: String,
	val spawnMessage: String,
	val rarity: Rarity,
	val islands: List<SkyBlockIsland> = emptyList(),
	val type: SeaCreatureType = SeaCreatureType.WATER,
) {
	val displayName: String by lazy { id.replace("_", "").title() }

	companion object {
		val CREATURES by SeaCreature::class.listFromRepository("fishing/sea_creatures.json")

		fun getBySpawnMessage(message: String): SeaCreature? = CREATURES.firstOrNull { it.spawnMessage == message }
	}
}