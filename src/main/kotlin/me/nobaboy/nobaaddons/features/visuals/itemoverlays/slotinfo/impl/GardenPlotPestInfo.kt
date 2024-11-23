package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.Position
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.SlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Formatting

object GardenPlotPestInfo : ISlotInfo {
	private const val ICON = "ൠ"
	private val PESTS = Regex("$ICON This plot has (?<count>\\d+) Pests?!")
	private val SPRAYED = Regex("Sprayed with \\D+ \\d+m \\d+s")
	override val enabled by config::gardenPests

	private fun drawPestCount(event: ScreenRenderEvents.DrawSlot) {
		val count = event.itemStack.lore.stringLines.firstNotNullOfOrNull(PESTS::matchEntire)?.groups["count"]?.value ?: return
		drawCount(event, count, Colors.RED)
	}

	private fun drawSprayonator(event: ScreenRenderEvents.DrawSlot) {
		if(!event.itemStack.lore.stringLines.any(SPRAYED::matches)) return
		drawInfo(event, SlotInfo(Text.literal(ICON).formatted(Formatting.GOLD, Formatting.BOLD), Position.TOP_LEFT))
	}

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		if(InventoryUtils.openInventoryName() != "Configure Plots") return
		if(!event.itemStack.name.string.startsWith("Plot -")) return

		drawPestCount(event)
		drawSprayonator(event)
	}
}