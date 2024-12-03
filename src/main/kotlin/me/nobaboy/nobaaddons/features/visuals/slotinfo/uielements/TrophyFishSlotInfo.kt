package me.nobaboy.nobaaddons.features.visuals.slotinfo.uielements

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.core.TrophyFishRarity
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.StringUtils.toAbbreviatedString
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TrophyFishSlotInfo : ISlotInfo {
	private val RARITY_REGEX = Regex("(?<rarity>Bronze|Silver|Gold|Diamond) [✔✖](?: \\((?<amount>[\\d,]+)\\))?")

	override val enabled: Boolean get() = config.trophyFish && SkyBlockAPI.inSkyBlock

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		if(InventoryUtils.openInventoryName() != "Trophy Fishing") return

		val fish = event.lore?.stringLines?.mapNotNull { RARITY_REGEX.matchEntire(it) } ?: return
		if(fish.isEmpty()) return
		val rarities: Map<TrophyFishRarity, Int> = fish.associate {
			// hypixel doesn't currently add commas to this, but just in case...
			val quantity = it.groups["amount"]?.value?.replace(",", "")?.toInt() ?: 0
			val rarity = it.groups["rarity"]!!.value
			TrophyFishRarity.entries.first { it.name.lowercaseEquals(rarity) } to quantity
		}

		if(config.checkMarkIfMaxed && TrophyFishRarity.entries.all { rarities[it]?.let { it > 0 } == true }) {
			drawCount(event, ISlotInfo.CHECK, NobaColor.GREEN.rgb)
			return
		}

		val total = rarities.values.sum()
		val highestRarity = TrophyFishRarity.entries.lastOrNull { rarities[it]?.let { it > 0 } == true } ?: TrophyFishRarity.BRONZE
		val nextRarity = TrophyFishRarity.BY_ID.apply(highestRarity.ordinal + 1)
		val text = Text.literal(total.toAbbreviatedString())
		if(nextRarity != highestRarity && nextRarity.pityAt?.let { total >= it - 100 } == true) { // TODO -100 for testing
			text.formatted(Formatting.BOLD)
		}

		drawCount(event, text, highestRarity.color.rgb)
	}
}