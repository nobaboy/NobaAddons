package me.nobaboy.nobaaddons.core.fishing

import me.nobaboy.nobaaddons.utils.NobaColor
import net.minecraft.util.Formatting

enum class TrophyFishRarity(val color: NobaColor, val pityAt: Int?, val formatting: Formatting) {
	BRONZE(NobaColor.DARK_GRAY, null, Formatting.DARK_GRAY),
	SILVER(NobaColor.GRAY, null, Formatting.GRAY),
	GOLD(NobaColor.GOLD, 100, Formatting.GOLD),
	DIAMOND(NobaColor.AQUA, 600, Formatting.AQUA);

	companion object {
		fun get(rarity: String) = entries.firstOrNull { it.name.equals(rarity, ignoreCase = true) }
	}
}