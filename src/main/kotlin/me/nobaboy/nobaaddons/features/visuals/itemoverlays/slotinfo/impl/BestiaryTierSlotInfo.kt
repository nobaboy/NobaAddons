package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.tryRomanToArabic
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.item.ItemStack

object BestiaryTierSlotInfo : ISlotInfo {
	override fun getStackOverlay(itemStack: ItemStack): String? {
		val inventoryName = InventoryUtils.openInventoryName() ?: return null
		val lore = itemStack.lore.stringLines

		if(!(inventoryName.startsWith("Bestiary ➜") || inventoryName.startsWith("Fishing ➜")) ||
			!lore.any { it.contains("Deaths: ") }
		) return null

		return itemStack.name.string.split(" ").lastOrNull()?.tryRomanToArabic()?.toString()
	}

	override fun isEnabled(): Boolean = SkyBlockAPI.inSkyblock && config.bestiaryTier
}