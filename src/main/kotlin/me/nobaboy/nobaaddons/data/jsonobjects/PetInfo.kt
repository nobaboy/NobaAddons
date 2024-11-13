package me.nobaboy.nobaaddons.data.jsonobjects

data class PetInfo(
	val type: String,
	val active: Boolean,
	val exp: Double,
	val tier: String,
	val heldItem: String?,
	val candyUsed: Int,
	val skin: String?,
	val uuid: String
)