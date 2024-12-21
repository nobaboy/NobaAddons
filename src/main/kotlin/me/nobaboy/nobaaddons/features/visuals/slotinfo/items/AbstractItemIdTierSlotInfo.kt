package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem

abstract class AbstractItemIdTierSlotInfo : ISlotInfo {
	protected abstract val pattern: Regex

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val item = event.itemStack.getSkyBlockItem() ?: return
		val tier = pattern.getGroupFromFullMatch(item.id, "tier") ?: return
		drawCount(event, tier)
	}
}