package me.nobaboy.nobaaddons.core.fishing

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.StringUtils.title

@Serializable
data class SeaCreature(
	val id: String,
	val name: String? = null,
	val spawnMessage: String,
	val rarity: Rarity,
	val islands: List<SkyBlockIsland> = emptyList(),
	val type: SeaCreatureType = SeaCreatureType.WATER,
) {
	val displayName: String by lazy { name ?: id.replace("_", " ").title() }

	companion object {
		val CREATURES by Repo.createList("fishing/sea_creatures.json", serializer())

		fun getBySpawnMessage(message: String): SeaCreature? = CREATURES.firstOrNull { it.spawnMessage == message }
	}
}