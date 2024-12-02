package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem

object PotionLevelSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.potionLevel

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val itemStack = event.itemStack
		if(itemStack.name.string.startsWith("Healer")) return

		val item = itemStack.getSkyBlockItem() ?: return
		if(item.id != "POTION") return

		val level = item.potionLevel.toString()
		drawCount(event, level)
	}
}