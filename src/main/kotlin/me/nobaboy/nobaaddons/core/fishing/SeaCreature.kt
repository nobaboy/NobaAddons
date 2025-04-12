package me.nobaboy.nobaaddons.core.fishing

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.StringUtils.title

@Serializable
data class SeaCreature(
	/**
	 * Internal ID of the sea creature
	 */
	val id: String,
	/**
	 * Optional name override; this defaults to the title-cased value of [id] if not provided.
	 */
	val name: String? = null,
	/**
	 * The chat message sent when this sea creature is caught
	 */
	val spawnMessage: String,
	/**
	 * If `true`, [spawnMessage] will be treated as a [Regex] pattern
	 */
	val spawnMessageRegex: Boolean = false,
	/**
	 * The [Rarity] of this sea creature
	 */
	val rarity: Rarity,
	/**
	 * All possible [SkyBlockIsland]s that this creature can be caught on
	 */
	val islands: List<SkyBlockIsland> = emptyList(),
	/**
	 * The possible zones that this creature can be caught in
	 */
	val zones: List<String>? = null,
	/**
	 * Whether this creature is caught from water(/ice) or lava fishing
	 */
	val type: SeaCreatureType = SeaCreatureType.WATER,
	/**
	 * Internal tags used by various features
	 */
	val tags: List<String> = emptyList(),
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
		val CREATURES by Repo.createList<SeaCreature>("fishing/sea_creatures.json")

		fun getBySpawnMessage(message: String): SeaCreature? = CREATURES?.firstOrNull { it.spawnMessageMatches(message) }
	}
}