package me.nobaboy.nobaaddons.core.dungeons

enum class DungeonBoss(val displayName: String?) {
	WATCHER("The Watcher"),
	BONZO("Bonzo"),
	SCARF("Scarf"),
	PROFESSOR("Professor"),
	THORN("Thorn"),
	LIVID("Livid"),
	SADAN("Sadan"),
	MAXOR("Maxor"),
	STORM("Storm"),
	GOLDOR("Goldor"),
	NECRON("Necron"),
	WITHER_KING("Wither King"),
	UNKNOWN(null),
	;

	companion object {
		fun getByName(name: String): DungeonBoss = entries.firstOrNull { it.displayName?.equals(name, ignoreCase = true) == true } ?: UNKNOWN
	}
}