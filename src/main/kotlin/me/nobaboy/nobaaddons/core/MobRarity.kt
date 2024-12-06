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

	fun isAtLeast(rarity: MobRarity): Boolean = this.ordinal >= rarity.ordinal
	fun isAtMost(rarity: MobRarity): Boolean = this.ordinal <= rarity.ordinal

	override fun toString(): String = name.title()
}