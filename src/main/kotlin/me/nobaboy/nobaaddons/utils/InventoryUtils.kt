package me.nobaboy.nobaaddons.utils

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.ItemStack

object InventoryUtils {
	fun openInventoryName(): String? = (MCUtils.client.currentScreen as? GenericContainerScreen)?.title?.string

	fun getInventoryItemsOrNull() = MCUtils.player?.inventory?.main
	fun getInventoryItems(): List<ItemStack> = getInventoryItemsOrNull()?.filterNotNull().orEmpty()

	fun getItemsInHotbar(): List<ItemStack> = getInventoryItemsOrNull()?.slice(0..8)?.filterNotNull().orEmpty()
}