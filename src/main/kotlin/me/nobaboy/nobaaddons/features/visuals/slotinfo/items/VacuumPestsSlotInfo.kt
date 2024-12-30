package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.NumberUtils.formatLong
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines

object VacuumPestsSlotInfo : ISlotInfo {
	private val vacuumPestsPattern by Regex("Vacuum Bag: (?<amount>\\d+) Pests").fromRepo("slot_info.vacuum_pests")

	// TODO: Could move to repo
	private val gardenVacuums = listOf(
		"SKYMART_VACUUM",
		"SKYMART_TURBO_VACUUM",
		"SKYMART_HYPER_VACUUM",
		"INFINI_VACUUM",
		"INFINI_VACUUM_HOOVERIUS"
	)

	override val enabled: Boolean get() = config.vacuumPests

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val itemStack = event.itemStack

		val item = itemStack.getSkyBlockItem() ?: return
		if(item.id !in gardenVacuums) return

		val lore = itemStack.lore.stringLines
		vacuumPestsPattern.firstFullMatch(lore) {
			var pests = groups["amount"]!!.value.formatLong()
			val count = when {
				pests < 1_000 -> "$pests"
				pests < 100_000 -> "${pests / 1_000}k"
				else -> "${pests / 100_000 / 10.0}m"
			}

			drawCount(event, count)
		}
	}
}