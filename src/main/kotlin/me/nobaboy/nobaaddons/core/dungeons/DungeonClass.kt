package me.nobaboy.nobaaddons.core.dungeons

enum class DungeonClass {
	HEALER,
	MAGE,
	BERSERK,
	ARCHER,
	TANK,
	UNKNOWN,
	;

	companion object {
		fun getByName(name: String): DungeonClass = entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: UNKNOWN
	}
}