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
	UNKNOWN(null);

	companion object {
		fun getByMessage(text: String): DungeonBoss = entries.firstOrNull { it.displayName != null && text.contains(it.displayName) } ?: UNKNOWN
	}
}