package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem

abstract class AbstractItemIdTierSlotInfo : ISlotInfo {
	protected abstract val pattern: Regex

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val item = event.itemStack.asSkyBlockItem ?: return
		val tier = pattern.getGroupFromFullMatch(item.id, "tier") ?: return
		drawCount(event, tier)
	}
}