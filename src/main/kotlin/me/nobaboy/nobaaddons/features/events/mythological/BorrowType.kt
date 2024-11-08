package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.utils.NobaColor

enum class BorrowType(val text: String, val color: NobaColor) {
	START("§aStart", NobaColor.GREEN),
	MOB("§cMob", NobaColor.RED),
	TREASURE("§6Treasure", NobaColor.GOLD),
	UNKNOWN("§7Unknown", NobaColor.GRAY),
}