package me.nobaboy.nobaaddons.features.visuals.slotinfo.uielements

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils

object SkyBlockLevelSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.skyBlockLevel

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		if(inventoryName != "SkyBlock Menu") return
		if(event.itemStack.name.string != "SkyBlock Leveling") return

		val level = SkyBlockAPI.level ?: return
		val color = SkyBlockAPI.getSkyBlockLevelColor(level)

		drawCount(event, level.toString(), color)
	}
}