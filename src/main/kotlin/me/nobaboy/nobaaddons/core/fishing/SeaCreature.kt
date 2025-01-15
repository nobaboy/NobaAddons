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
	val spawnMessageRegex: Boolean = false,
	val rarity: Rarity,
	val islands: List<SkyBlockIsland> = emptyList(),
	val zones: List<String>? = null,
	val type: SeaCreatureType = SeaCreatureType.WATER,
) {
	val displayName: String by lazy { name ?: id.replace("_", " ").title() }

	private val regex: Regex? by lazy { if(spawnMessageRegex) Regex(spawnMessage) else null }

	fun spawnMessageMatches(message: String): Boolean {
		return when(val regex = this.regex) {
			null -> spawnMessage == message
			else -> regex.matches(message)
		}
	}

	companion object {
		val CREATURES by Repo.createList("fishing/sea_creatures.json", serializer())

		fun getBySpawnMessage(message: String): SeaCreature? = CREATURES.firstOrNull { it.spawnMessageMatches(message) }
	}
}