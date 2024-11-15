package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.utils.NobaColor

enum class BorrowType(val text: String, val color: NobaColor) {
	START("Start", NobaColor.GREEN),
	MOB("Mob", NobaColor.RED),
	TREASURE("Treasure", NobaColor.GOLD),
	UNKNOWN("Unknown", NobaColor.GRAY)
}