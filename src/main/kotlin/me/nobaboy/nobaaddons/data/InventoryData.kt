package me.nobaboy.nobaaddons.data

import net.minecraft.item.ItemStack

data class InventoryData(
	val windowId: Int,
	val title: String,
	val slotCount: Int,
	val items: MutableMap<Int, ItemStack>,
	var ready: Boolean = false
)