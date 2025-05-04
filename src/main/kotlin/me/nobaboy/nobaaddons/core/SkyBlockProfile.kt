package me.nobaboy.nobaaddons.core

enum class SkyBlockProfile {
	CLASSIC,
	IRONMAN,
	STRANDED,
	BINGO,
	;

	companion object {
		fun getByName(name: String): SkyBlockProfile = entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: CLASSIC
	}
}