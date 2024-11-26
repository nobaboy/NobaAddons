package me.nobaboy.nobaaddons.data

import me.nobaboy.nobaaddons.core.ItemRarity

data class PetData(
	val name: String,
	val id: String,
	val level: Int,
	val xp: Double,
	val rarity: ItemRarity,
	val candy: Int = 0,
	val active: Boolean = false,
	val heldItem: String? = null,
	val uuid: String? = null
) {
	val maxLevel: Int = if(id == "GOLDEN_DRAGON") 200 else 100
}