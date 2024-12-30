package me.nobaboy.nobaaddons.features.visuals.slotinfo.uielements

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NumberUtils.tryRomanToArabic
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines

object SkillLevelSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.skillLevel

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		if(inventoryName != "Your Skills") return

		val itemStack = event.itemStack
		if(itemStack.name.string == "Dungeoneering") return

		val lore = itemStack.lore.stringLines
		if(lore.none { it == "Click to view!" }) return

		if(config.checkMarkIfMaxed && lore.none { it.startsWith("Progress to") }) {
			drawCount(event, "✔", NobaColor.GREEN)
		} else {
			val tier = itemStack.name.string.split(" ").lastOrNull()?.tryRomanToArabic()?.toString() ?: "0"
			drawCount(event, tier)
		}
	}
}