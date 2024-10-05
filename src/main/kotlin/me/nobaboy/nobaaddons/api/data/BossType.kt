package me.nobaboy.nobaaddons.api.data

enum class BossType(val displayName: String) {
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
        fun fromChat(text: String): BossType {
            for (boss in entries) {
                if (text.contains(boss.displayName)) return boss
            }
            return UNKNOWN
        }
    }
}