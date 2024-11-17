package me.nobaboy.nobaaddons.data.json

data class MayorJson(
	val mayor: MayorInfo,
	val current: MayorElection?
)

data class MayorInfo(
	val key: String,
	val name: String,
	val perks: List<Perk>,
	val minister: Minister?, // Special mayors don't have ministers
	val election: MayorElection
)

data class Minister(
	val key: String,
	val name: String,
	val perk: Perk
)

data class Perk(
	val name: String,
	val description: String,
	val minister: Boolean = false
)

data class MayorElection(
	val year: Int,
	val candidates: List<MayorCandidate>
)

data class MayorCandidate(
	val key: String,
	val name: String,
	val perks: List<Perk>,
	val votes: Int,
)