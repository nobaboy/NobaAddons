package me.nobaboy.nobaaddons.core

import me.nobaboy.nobaaddons.utils.NobaColor
import net.minecraft.util.function.ValueLists

enum class TrophyFishRarity(val color: NobaColor, val pityAt: Int?) {
	BRONZE(NobaColor.DARK_GRAY, null),
	SILVER(NobaColor.GRAY, null),
	GOLD(NobaColor.GOLD, 100),
	DIAMOND(NobaColor.AQUA, 600);

	companion object {
		val BY_ID = ValueLists.createIdToValueFunction(TrophyFishRarity::ordinal, entries.toTypedArray(), ValueLists.OutOfBoundsHandling.CLAMP)
	}
}