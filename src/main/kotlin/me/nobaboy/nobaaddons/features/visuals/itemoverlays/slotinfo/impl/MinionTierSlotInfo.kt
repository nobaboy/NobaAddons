package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import java.util.regex.Pattern

object MinionTierSlotInfo : ISlotInfo {
	private val minionIdPattern = Pattern.compile("^[A-Z_]+_GENERATOR_(?<tier>\\d+)")

	override val enabled: Boolean get() = config.minionTier

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val item = event.itemStack.getSkyBlockItem() ?: return
		minionIdPattern.matchMatcher(item.id) {
			drawCount(event, group("tier"))
		}
	}
}