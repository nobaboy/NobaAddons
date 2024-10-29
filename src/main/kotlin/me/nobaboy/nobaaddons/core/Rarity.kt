package me.nobaboy.nobaaddons.core

enum class Rarity {
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
		val rarities = Rarity.entries.associateBy { it.name.replace("_", " ") }
	}
}