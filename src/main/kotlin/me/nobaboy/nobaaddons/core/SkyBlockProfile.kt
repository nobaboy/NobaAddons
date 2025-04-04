package me.nobaboy.nobaaddons.core

enum class SkyBlockProfile(val displayName: String) {
	CLASSIC("Classic"),
	IRONMAN("Ironman"),
	STRANDED("Stranded"),
	BINGO("Bingo"),
	;

	companion object {
		fun getByName(name: String): SkyBlockProfile = entries.firstOrNull { it.displayName == name } ?: CLASSIC
	}
}