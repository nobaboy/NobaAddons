package me.nobaboy.nobaaddons.core

enum class ItemRarity {
	COMMON,
	UNCOMMON,
	RARE,
	EPIC,
	LEGENDARY,
	MYTHIC,
	DIVINE,
	SPECIAL,
	VERY_SPECIAL,
	ULTIMATE,
	ADMIN,
	UNKNOWN;

	companion object {
		val rarities = entries.associateBy { it.name.replace("_", " ") }
	}
}