package me.nobaboy.nobaaddons.core.dungeons

enum class DungeonBoss(val displayName: String) {
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
	UNKNOWN("");

	companion object {
		fun fromChat(text: String): DungeonBoss {
			for(boss in entries) {
				if(text.contains(boss.displayName)) return boss
			}
			return UNKNOWN
		}
	}
}