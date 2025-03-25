package me.nobaboy.nobaaddons.features.inventory.slotinfo.items

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem

object DungeonHeadTierSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.dungeonHeadTier

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val item = event.itemStack.asSkyBlockItem ?: return
		if(!(item.id.startsWith("GOLD_") || item.id.startsWith("DIAMOND_")) || !item.id.endsWith("_HEAD")) return

		val tier = getHeadTier(item.id)
		drawCount(event, tier)
	}

	private fun getHeadTier(id: String): String {
		return when {
			id.contains("BONZO") -> "1"
			id.contains("SCARF") -> "2"
			id.contains("PROFESSOR") -> "3"
			id.contains("THORN") -> "4"
			id.contains("LIVID") -> "5"
			id.contains("SADAN") -> "6"
			id.contains("NECRON") -> "7"
			else -> "?"
		}
	}
}