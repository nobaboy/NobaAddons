package me.nobaboy.nobaaddons.core.dungeons

enum class DungeonFloor(val abbreviation: String?, val number: Int?) {
	ENTRANCE("E", 0),

	FLOOR_ONE("F1", 1),
	FLOOR_TWO("F2", 2),
	FLOOR_THREE("F3", 3),
	FLOOR_FOUR("F4", 4),
	FLOOR_FIVE("F5", 5),
	FLOOR_SIX("F6", 6),
	FLOOR_SEVEN("F7", 7),

	MASTER_ONE("M1", 1),
	MASTER_TWO("M2", 2),
	MASTER_THREE("M3", 3),
	MASTER_FOUR("M4", 4),
	MASTER_FIVE("M5", 5),
	MASTER_SIX("M6", 6),
	MASTER_SEVEN("M7", 7),

	UNKNOWN(null, null),
	;

	companion object {
		fun getByAbbreviation(abbreviation: String): DungeonFloor = entries.firstOrNull { it.abbreviation?.equals(abbreviation, ignoreCase = true) == true } ?: UNKNOWN
	}
}