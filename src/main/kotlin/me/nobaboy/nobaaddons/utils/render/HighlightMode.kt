package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class HighlightMode {
	OUTLINE,
	FILLED,
	FILLED_OUTLINE;

	override fun toString(): String = name.replace("_", " ").title()
}