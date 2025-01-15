package me.nobaboy.nobaaddons.features.visuals.slotinfo.uielements

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines

object TuningPointsSlotInfo : ISlotInfo {
	private val tuningPointsPattern by Regex("^Stat has: (?<points>\\d+) points?").fromRepo("slot_info.tuning_points")

	override val enabled: Boolean get() = config.tuningPoints

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		if(inventoryName != "Stats Tuning") return

		val lore = event.itemStack.lore.stringLines
		tuningPointsPattern.firstFullMatch(lore) {
			val points = groups["points"]!!.value
			if(points == "0") return

			drawCount(event, points)
		}
	}
}