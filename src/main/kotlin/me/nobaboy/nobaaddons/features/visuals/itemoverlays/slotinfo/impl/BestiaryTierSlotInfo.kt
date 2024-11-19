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

		if(!(inventoryName.startsWith("Bestiary ➜") || inventoryName.startsWith("Fishing ➜"))) return
		if(lore.none { it.contains("Deaths: ") }) return

		val tier = if(lore.none { it == "Overall Progress: 100% (MAX!)" }) {
			itemStack.name.string.split(" ").lastOrNull()?.tryRomanToArabic()?.toString() ?: "0"
		} else {
			"✔"
		}

		drawCount(event, tier, if(tier == "✔") Colors.GREEN else Colors.WHITE)
	}
}