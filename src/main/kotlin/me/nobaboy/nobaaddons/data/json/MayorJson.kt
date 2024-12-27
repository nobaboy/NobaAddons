package me.nobaboy.nobaaddons.data.json

import kotlinx.serialization.Serializable

@Serializable
data class MayorJson(
	val mayor: MayorInfo,
	val current: MayorElection?
)

@Serializable
data class MayorInfo(
	val key: String,
	val name: String,
	val perks: List<Perk>,
	val minister: Minister?, // Special mayors don't have ministers
	val election: MayorElection
)

@Serializable
data class Minister(
	val key: String,
	val name: String,
	val perk: Perk
)

@Serializable
data class Perk(
	val name: String,
	val description: String,
	val minister: Boolean = false
)

@Serializable
data class MayorElection(
	val year: Int,
	val candidates: List<MayorCandidate>
)

@Serializable
data class MayorCandidate(
	val key: String,
	val name: String,
	val perks: List<Perk>,
	val votes: Int? = null
)