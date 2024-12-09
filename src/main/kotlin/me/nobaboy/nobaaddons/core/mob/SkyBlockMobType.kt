package me.nobaboy.nobaaddons.core.mob

enum class SkyBlockMobType {
	BASIC,
	DUNGEON,
	SLAYER,
	PLAYER,
	BOSS,
	NPC;

	fun isSkyBlockMob() = when(this) {
		BASIC, DUNGEON, SLAYER, BOSS -> true
		else -> false
	}
}