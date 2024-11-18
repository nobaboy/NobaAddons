package me.nobaboy.nobaaddons.utils

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen

object InventoryUtils {
	fun openInventoryName(): String? = (MCUtils.client.currentScreen as? GenericContainerScreen)?.title?.string
}