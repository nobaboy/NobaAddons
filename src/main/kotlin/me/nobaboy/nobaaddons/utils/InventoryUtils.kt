package me.nobaboy.nobaaddons.utils

//? if >=1.21.5 {
/*import me.nobaboy.nobaaddons.mixins.accessors.PlayerInventoryAccessor
*///?}

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.ItemStack

object InventoryUtils {
	fun openInventoryName(): String? = (MCUtils.client.currentScreen as? GenericContainerScreen)?.title?.string

	fun getInventoryItems(): List<ItemStack> = getInventoryItemsOrNull().orEmpty()
	fun getHotbarItems(): List<ItemStack> = getInventoryItemsOrNull()?.slice(0..8).orEmpty()

	fun getInventoryItemsOrNull(): List<ItemStack>? {
		//? if >=1.21.5 {
		/*return (MCUtils.player?.inventory as? PlayerInventoryAccessor)?.main
		*///?} else {
		return MCUtils.player?.inventory?.main
		//?}
	}
}