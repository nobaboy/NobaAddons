package me.nobaboy.nobaaddons.core

enum class SkyBlockMobType {
	BASIC,
	DUNGEON,
	BOSS,
	SLAYER,
	NPC;

	fun isSkyBlockMob() = when(this) {
		BASIC, DUNGEON, BOSS, SLAYER -> true
		else -> false
	}
}