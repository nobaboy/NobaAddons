package me.nobaboy.nobaaddons.features.inventory.slotinfo.uielements

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.fishing.TrophyFishAPI
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.toAbbreviatedString
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TrophyFishSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.trophyFish && SkyBlockAPI.inSkyBlock

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		if(InventoryUtils.openInventoryName() != "Trophy Fishing") return

		val rarities = TrophyFishAPI.getCountFromOdgerStack(event.itemStack)
		if(rarities.isEmpty()) return

		if(config.checkMarkIfMaxed && TrophyFishRarity.entries.all { rarities[it]?.let { it > 0 } == true }) {
			drawCount(event, ISlotInfo.CHECK, NobaColor.GREEN)
			return
		}

		val total = rarities.values.sum()
		val highestRarity = TrophyFishRarity.entries.lastOrNull { rarities[it]?.let { it > 0 } == true } ?: TrophyFishRarity.BRONZE
		val text = Text.literal(total.toAbbreviatedString())
		val nextCatchIsPity = TrophyFishRarity.entries.any { it.pityAt?.let { total >= it - 1 } == true && (rarities[it] ?: 0) == 0 }
		if(nextCatchIsPity) text.formatted(Formatting.BOLD)

		drawCount(event, text, highestRarity.color)
	}
}