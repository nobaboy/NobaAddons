package me.nobaboy.nobaaddons.core

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.title

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

	val colorCode by lazy { color?.colorCode }

	fun isAtLeast(rarity: ItemRarity): Boolean = this.ordinal >= rarity.ordinal

	override fun toString(): String = name.replace("_", " ").title()

	companion object {
		val RARITIES = entries.associateBy { it.name.replace("_", " ") }

		fun getRarity(text: String): ItemRarity = RARITIES.getOrDefault(text, UNKNOWN)
		fun getByColorCode(colorCode: Char): ItemRarity = entries.firstOrNull { it.colorCode == colorCode } ?: UNKNOWN
	}
}