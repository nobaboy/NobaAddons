package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.firstMatcher
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object GardenPlotPestInfo : ISlotInfo {
	private const val ICON = "ൠ"

	private val pestsPattern = Pattern.compile("$ICON This plot has (?<count>\\d+) Pests?!")

	override val enabled: Boolean get() = config.gardenPests

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		if(InventoryUtils.openInventoryName() != "Configure Plots") return

		val itemStack = event.itemStack
		if(!itemStack.name.string.startsWith("Plot -")) return

		val lore = itemStack.lore.stringLines
		pestsPattern.firstMatcher(lore) {
			drawCount(event, group("count"), NobaColor.RED.toColor().rgb)
		}

		if(lore.any { it.startsWith("Sprayed with") })
			drawInfo(event, Text.literal(ICON).formatted(Formatting.GOLD, Formatting.BOLD))
	}
}