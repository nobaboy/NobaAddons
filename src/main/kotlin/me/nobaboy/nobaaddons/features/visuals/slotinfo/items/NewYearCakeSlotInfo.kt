package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem

object NewYearCakeSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.newYearCake

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val item = event.itemStack.getSkyBlockItem() ?: return
		if(item.id != "NEW_YEAR_CAKE") return

		drawCount(event, item.newYearsCake.toString())
	}
}