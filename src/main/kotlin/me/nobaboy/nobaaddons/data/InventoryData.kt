package me.nobaboy.nobaaddons.data

import net.minecraft.item.ItemStack

data class InventoryData(
	val id: Int,
	val title: String,
	val slotCount: Int,
	val items: MutableMap<Int, ItemStack>,
)