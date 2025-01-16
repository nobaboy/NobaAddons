package me.nobaboy.nobaaddons.features.visuals.slotinfo.uielements

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object GardenPlotPestInfo : ISlotInfo {
	private const val ICON = "ൠ"

	private val pestsPattern by Regex("ൠ This plot has (?<count>\\d+) Pests?!").fromRepo("slot_info.desk_pest_count")
	private val sprayedWith by "Sprayed with".fromRepo("slot_info.desk_sprayed_with_prefix")

	override val enabled: Boolean get() = config.gardenPlotPests

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		if(InventoryUtils.openInventoryName() != "Configure Plots") return

		val itemStack = event.itemStack
		if(!itemStack.name.string.startsWith("Plot -")) return

		val lore = itemStack.lore.stringLines
		pestsPattern.firstFullMatch(lore) {
			drawCount(event, groups["count"]!!.value, NobaColor.RED)
		}

		if(lore.any { it.startsWith(sprayedWith) }) {
			drawInfo(event, Text.literal(ICON).formatted(Formatting.GOLD, Formatting.BOLD))
		}
	}
}