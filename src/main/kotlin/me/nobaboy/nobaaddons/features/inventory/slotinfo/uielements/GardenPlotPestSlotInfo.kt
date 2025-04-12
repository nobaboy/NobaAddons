package me.nobaboy.nobaaddons.features.inventory.slotinfo.uielements

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object GardenPlotPestSlotInfo : ISlotInfo {
	private const val ICON = "ൠ"

	private val PEST_COUNT_REGEX by Repo.regex(
		"slot_info.desk_pest_count",
		"ൠ This plot has (?<count>\\d+) Pests?!"
	)
	private val sprayedWith by Repo.string("slot_info.desk_sprayed_with_prefix", "Sprayed with")

	override val enabled: Boolean get() = config.gardenPlotPests

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		if(InventoryUtils.openInventoryName() != "Configure Plots") return

		val itemStack = event.itemStack
		if(!itemStack.name.string.startsWith("Plot -")) return

		val lore = itemStack.lore.stringLines
		PEST_COUNT_REGEX.firstFullMatch(lore) {
			drawCount(event, groups["count"]!!.value, NobaColor.RED)
		}

		if(lore.any { it.startsWith(sprayedWith) }) {
			drawInfo(event, Text.literal(ICON).formatted(Formatting.GOLD, Formatting.BOLD))
		}
	}
}