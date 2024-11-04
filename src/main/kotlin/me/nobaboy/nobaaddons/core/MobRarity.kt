package me.nobaboy.nobaaddons.core

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class MobRarity(val color: NobaColor) {
	COMMON(NobaColor.WHITE),
	UNCOMMON(NobaColor.GREEN),
	RARE(NobaColor.BLUE),
	EPIC(NobaColor.DARK_PURPLE),
	LEGENDARY(NobaColor.GOLD),
	MYTHIC(NobaColor.LIGHT_PURPLE);

	val displayName = name.replace("_", " ").title()

	fun isAtLeast(rarity: MobRarity): Boolean = this.ordinal >= rarity.ordinal
}