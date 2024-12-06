package me.nobaboy.nobaaddons.core

import me.nobaboy.nobaaddons.utils.NobaColor

enum class TrophyFishRarity(val color: NobaColor, val pityAt: Int?) {
	BRONZE(NobaColor.DARK_GRAY, null),
	SILVER(NobaColor.GRAY, null),
	GOLD(NobaColor.GOLD, 100),
	DIAMOND(NobaColor.AQUA, 600)
}