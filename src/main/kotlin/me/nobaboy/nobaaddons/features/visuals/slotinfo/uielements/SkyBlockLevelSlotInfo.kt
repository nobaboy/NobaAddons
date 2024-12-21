package me.nobaboy.nobaaddons.features.visuals.slotinfo.uielements

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines

object SkyBlockLevelSlotInfo : ISlotInfo {
	private val skyBlockLevelPattern by Regex("^Your SkyBlock Level: \\[(?<level>\\d+)]").fromRepo("slot_info.skyblock_level")

	override val enabled: Boolean get() = config.skyBlockLevel

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val inventoryName = InventoryUtils.openInventoryName() ?: return
		if(inventoryName != "SkyBlock Menu") return

		val itemStack = event.itemStack
		if(itemStack.name.string != "SkyBlock Leveling") return

		val lore = itemStack.lore.stringLines
		skyBlockLevelPattern.firstFullMatch(lore) {
			val level = groups["level"]!!.value
			val color = getSkyBlockLevelColor(level.toInt()).toColor().rgb

			drawCount(event, level, color)
		}
	}

	private fun getSkyBlockLevelColor(level: Int): NobaColor {
		return when(level) {
			in 0 until 40 -> NobaColor.GRAY
			in 40 until 80 -> NobaColor.WHITE
			in 80 until 120 -> NobaColor.YELLOW
			in 120 until 160 -> NobaColor.GREEN
			in 160 until 200 -> NobaColor.DARK_GREEN
			in 200 until 240 -> NobaColor.AQUA
			in 240 until 280 -> NobaColor.DARK_AQUA
			in 280 until 320 -> NobaColor.BLUE
			in 320 until 360 -> NobaColor.LIGHT_PURPLE
			in 360 until 400 -> NobaColor.DARK_PURPLE
			in 400 until 440 -> NobaColor.GOLD
			in 440 until 480 -> NobaColor.RED
			in 480 until 520 -> NobaColor.DARK_RED
			else -> NobaColor.BLACK
		}
	}
}