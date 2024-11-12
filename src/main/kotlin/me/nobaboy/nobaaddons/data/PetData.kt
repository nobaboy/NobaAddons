package me.nobaboy.nobaaddons.data

import me.nobaboy.nobaaddons.core.ItemRarity

data class PetData(
	val id: String,
	val level: Int,
	val xp: Double,
	val tier: ItemRarity,
	val heldItem: String
)