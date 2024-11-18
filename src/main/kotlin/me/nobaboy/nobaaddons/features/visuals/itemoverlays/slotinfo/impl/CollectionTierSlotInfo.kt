package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.tryRomanToArabic
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.item.ItemStack

object CollectionTierSlotInfo : ISlotInfo {
	override fun getStackOverlay(itemStack: ItemStack): String? {
		if(InventoryUtils.openInventoryName()?.endsWith(" Collections") != true) return null

		val lore = itemStack.lore.stringLines
		if(!lore.reversed().any { it == "Click to view!" }) return null

		val tier = itemStack.name.string.split(" ").lastOrNull() ?: return "0"
		return tier.tryRomanToArabic().toString()
	}

	override fun isEnabled(): Boolean = SkyBlockAPI.inSkyblock && config.collectionTier
}