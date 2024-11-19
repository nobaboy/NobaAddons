package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.tryRomanToArabic
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.util.Colors

object BestiaryTierSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.bestiaryTier

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		val itemStack = event.itemStack
		val lore = itemStack.lore.stringLines

		if(!(inventoryName.contains("Bestiary ➜") || inventoryName.contains("Fishing ➜"))) return
		if(lore.none { it.contains("Deaths: ") }) return

		if(config.checkMarkIfMaxed && lore.any { it == "Overall Progress: 100% (MAX!)" }) {
			drawCount(event, "✔", Colors.GREEN)
		} else {
			val tier = itemStack.name.string.split(" ").lastOrNull()?.tryRomanToArabic()?.toString() ?: "0"
			drawCount(event, tier)
		}
	}
}