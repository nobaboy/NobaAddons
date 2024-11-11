package me.nobaboy.nobaaddons.data

import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.utils.items.SkyBlockItemData

data class PetData(
	val item: SkyBlockItemData,
	val name: String,
//	val xp: Double,
	val level: Int,
	val rarity: ItemRarity = item.rarity
)