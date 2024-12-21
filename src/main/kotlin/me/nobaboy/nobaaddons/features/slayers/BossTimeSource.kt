package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class BossTimeSource {
	REAL_TIME,
	BOSS_TIME_REMAINING;

	override fun toString(): String = name.replace("_", " ").title()
}