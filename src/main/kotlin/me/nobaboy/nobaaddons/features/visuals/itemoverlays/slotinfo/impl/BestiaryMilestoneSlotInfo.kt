package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.tryRomanToArabic
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines

object BestiaryMilestoneSlotInfo : ISlotInfo {
	override val enabled get() = SkyBlockAPI.inSkyBlock && config.bestiaryMilestone

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		val itemStack = event.itemStack
		val lore = itemStack.lore.stringLines

		if(!(inventoryName.startsWith("Bestiary") || inventoryName.startsWith("Fishing ➜"))) return
		if(lore.none { it == "Click to open Bestiary Milestones!" }) return

		val milestone = itemStack.name.string.split(" ").lastOrNull()?.tryRomanToArabic().toString()
		drawCount(event, milestone)
	}
}