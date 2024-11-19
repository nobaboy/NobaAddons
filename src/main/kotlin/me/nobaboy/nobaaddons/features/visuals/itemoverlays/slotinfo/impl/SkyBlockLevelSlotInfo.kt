package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.firstMatcher
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import java.util.regex.Pattern

object SkyBlockLevelSlotInfo : ISlotInfo {
	private val skyBlockLevelPattern = Pattern.compile("Your SkyBlock Level: \\[(?<level>\\d+)]")

	override val enabled: Boolean get() = config.skyBlockLevel

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		if(InventoryUtils.openInventoryName() != "SkyBlock Menu") return

		val itemStack = event.itemStack
		if(itemStack.name.string != "SkyBlock Leveling") return

		val lore = itemStack.lore.stringLines
		skyBlockLevelPattern.firstMatcher(lore) {
			val color = getSkyBlockLevelColor(group("level").toInt()).toColor().rgb
			drawCount(event, group("level"), color)
		}
	}

	private fun getSkyBlockLevelColor(level: Int): NobaColor {
		return when(level) {
			in 0..39 -> NobaColor.GRAY
			in 40..79 -> NobaColor.WHITE
			in 80..119 -> NobaColor.YELLOW
			in 120..159 -> NobaColor.GREEN
			in 160..199 -> NobaColor.DARK_GREEN
			in 200..239 -> NobaColor.AQUA
			in 240..279 -> NobaColor.DARK_AQUA
			in 280..319 -> NobaColor.BLUE
			in 320..359 -> NobaColor.LIGHT_PURPLE
			in 360..399 -> NobaColor.DARK_PURPLE
			in 400..439 -> NobaColor.GOLD
			in 440..479 -> NobaColor.RED
			in 480..519 -> NobaColor.DARK_RED
			else -> NobaColor.BLACK
		}
	}
}