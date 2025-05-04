package me.nobaboy.nobaaddons.data.json

import kotlinx.serialization.Serializable

@Serializable
data class PetNbt(
	val type: String,
	val active: Boolean,
	val exp: Double,
	val tier: String,
	val heldItem: String? = null,
	val candyUsed: Int,
	val skin: String? = null,
)