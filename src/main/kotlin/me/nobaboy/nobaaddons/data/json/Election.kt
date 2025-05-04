package me.nobaboy.nobaaddons.data.json

import kotlinx.serialization.Serializable

@Serializable
data class Election(
	val mayor: Mayor,
)

@Serializable
data class Mayor(
	val name: String,
	val perks: List<Perk>,
	val minister: Minister? = null, // Special mayors don't have ministers
)

@Serializable
data class Minister(
	val name: String,
	val perk: Perk,
)

@Serializable
data class Perk(
	val name: String,
	val description: String,
	val minister: Boolean = false,
)