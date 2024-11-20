package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.tryRomanToArabic
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.util.Colors

object BestiarySlotInfo : ISlotInfo {
	private var bestiaryLocation: String? = null

	override val enabled get() = config.bestiaryTier || config.bestiaryMilestone

	private fun getTier(name: String): String {
		return name.split(" ").lastOrNull()?.tryRomanToArabic()?.toString() ?: "0"
	}

	private fun getBestiaryLocation(inventoryName: String): String? {
		return when {
			inventoryName == "Bestiary" -> inventoryName
			inventoryName.contains("Bestiary ➜") -> inventoryName.split(" ➜ ").getOrNull(1)
			bestiaryLocation != null && inventoryName.contains("$bestiaryLocation ➜") -> bestiaryLocation
			else -> null
		}
	}

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		val itemStack = event.itemStack
		val lore = itemStack.lore.stringLines

		bestiaryLocation = getBestiaryLocation(inventoryName) ?: return

		if (config.bestiaryTier && lore.any { it.endsWith("Bonuses") }) {
			if (config.checkMarkIfMaxed && lore.any { it == "Overall Progress: 100% (MAX!)" }) {
				drawCount(event, "✔", Colors.GREEN)
			} else {
				val tier = getTier(itemStack.name.string)
				drawCount(event, tier)
			}
		}

		if (config.bestiaryMilestone && lore.any { it == "Click to open Bestiary Milestones!" }) {
			val milestone = getTier(itemStack.name.string)
			drawCount(event, milestone)
		}
	}
}