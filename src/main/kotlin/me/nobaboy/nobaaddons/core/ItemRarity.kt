package me.nobaboy.nobaaddons.core

import me.nobaboy.nobaaddons.utils.NobaColor

enum class ItemRarity(val color: NobaColor? = null) {
	COMMON(NobaColor.WHITE),
	UNCOMMON(NobaColor.GREEN),
	RARE(NobaColor.BLUE),
	EPIC(NobaColor.DARK_PURPLE),
	LEGENDARY(NobaColor.GOLD),
	MYTHIC(NobaColor.LIGHT_PURPLE),
	DIVINE(NobaColor.AQUA),
	SPECIAL(NobaColor.RED),
	VERY_SPECIAL(NobaColor.RED),
	ULTIMATE(NobaColor.DARK_RED),
	ADMIN(NobaColor.RED),
	UNKNOWN;

	fun isAtLeast(rarity: ItemRarity): Boolean = this.ordinal >= rarity.ordinal

	companion object {
		val rarities = entries.associateBy { it.name.replace("_", " ") }
	}
}