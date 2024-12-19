package me.nobaboy.nobaaddons.core.fishing

import me.nobaboy.nobaaddons.utils.NobaColor

enum class TrophyFishRarity(val color: NobaColor, val pityAt: Int?) {
	BRONZE(NobaColor.DARK_GRAY, null),
	SILVER(NobaColor.GRAY, null),
	GOLD(NobaColor.GOLD, 100),
	DIAMOND(NobaColor.AQUA, 600);

	val formatting = color.toFormatting()

	companion object {
		fun get(rarity: String) = entries.firstOrNull { it.name.equals(rarity, ignoreCase = true) }
	}
}