package me.nobaboy.nobaaddons.data

import me.nobaboy.nobaaddons.core.ItemRarity

data class PetData(
	val name: String,
	val id: String,
	val level: Int,
	val xp: Double,
	val rarity: ItemRarity,
	val heldItem: String? = null,
	val uuid: String? = null
)