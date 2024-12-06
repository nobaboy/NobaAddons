package me.nobaboy.nobaaddons.screens.hud.elements

import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class TextMode {
	PURE,
	SHADOW,
	OUTLINE;

	override fun toString(): String = name.title()
}