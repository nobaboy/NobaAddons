package me.nobaboy.nobaaddons.features.inventory.slotinfo.items

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.NumberUtils.parseLong
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines

// TODO Add cap option of 40 (which is max fortune), and consider red !!! if >=100k
object VacuumPestsSlotInfo : ISlotInfo {
	private val VACUUM_PESTS_REGEX by Regex("^Vacuum Bag: (?<amount>[\\d,]+) Pests").fromRepo("slot_info.vacuum_pests")

	private val gardenVacuums = listOf(
		"SKYMART_VACUUM",
		"SKYMART_TURBO_VACUUM",
		"SKYMART_HYPER_VACUUM",
		"INFINI_VACUUM",
		"INFINI_VACUUM_HOOVERIUS"
	)

	override val enabled: Boolean get() = config.vacuumPests

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val itemStack = event.itemStack

		val item = itemStack.asSkyBlockItem ?: return
		if(item.id !in gardenVacuums) return

		val lore = itemStack.lore.stringLines

		// This doesn't use toAbbreviatedString() here since 4 characters would extend past the slot,
		// so we're compacting down to millions at >=100k
		VACUUM_PESTS_REGEX.firstFullMatch(lore) {
			val pests = groups["amount"]!!.value.parseLong()
			val count = when {
				pests < 1_000 -> "$pests"
				pests < 100_000 -> "${pests / 1_000}k"
				else -> "${pests / 100_000 / 10.0}m"
			}

			drawCount(event, count)
		}
	}
}