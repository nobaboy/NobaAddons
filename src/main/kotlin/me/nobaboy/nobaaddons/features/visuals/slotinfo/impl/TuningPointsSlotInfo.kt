package me.nobaboy.nobaaddons.features.visuals.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.firstMatcher
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import java.util.regex.Pattern

object TuningPointsSlotInfo : ISlotInfo {
	private val tuningPointsPattern = Pattern.compile("^Stat has: (?<points>\\d+) points?")

	override val enabled: Boolean get() = config.tuningPoints

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		if(inventoryName != "Stats Tuning") return

		val lore = event.itemStack.lore.stringLines
		tuningPointsPattern.firstMatcher(lore) {
			val points = group("points")
			if(points == "0") return

			drawCount(event, points)
		}
	}
}