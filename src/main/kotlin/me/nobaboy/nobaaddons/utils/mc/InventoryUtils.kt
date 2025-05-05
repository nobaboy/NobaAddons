package me.nobaboy.nobaaddons.utils.mc

//? if >=1.21.5 {
/*import me.nobaboy.nobaaddons.mixins.accessors.PlayerInventoryAccessor
*///?}

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.ItemStack

object InventoryUtils {
	fun openInventoryName(): String? = (MCUtils.client.currentScreen as? GenericContainerScreen)?.title?.string

	fun getInventoryItemsOrNull(): List<ItemStack>? {
		//? if >=1.21.5 {
		/*return (MCUtils.player?.inventory as? PlayerInventoryAccessor)?.main
		*///?} else {
		return MCUtils.player?.inventory?.main
		//?}
	}
	fun getInventoryItems(): List<ItemStack> = getInventoryItemsOrNull()?.filterNotNull().orEmpty()

	fun getItemsInHotbar(): List<ItemStack> = getInventoryItemsOrNull()?.slice(0..8)?.filterNotNull().orEmpty()
}