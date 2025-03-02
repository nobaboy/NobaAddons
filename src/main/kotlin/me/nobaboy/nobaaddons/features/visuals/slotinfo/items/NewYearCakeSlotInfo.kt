package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem

object NewYearCakeSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.newYearCake

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val item = event.itemStack.asSkyBlockItem ?: return
		if(item.id != "NEW_YEAR_CAKE") return

		drawCount(event, item.newYearsCake.toString())
	}
}