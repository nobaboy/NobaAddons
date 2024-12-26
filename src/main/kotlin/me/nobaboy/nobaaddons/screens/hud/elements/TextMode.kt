package me.nobaboy.nobaaddons.screens.hud.elements

import me.nobaboy.nobaaddons.utils.EnumUtils
import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class TextMode {
	PURE,
	SHADOW,
	OUTLINE;

	val next: TextMode by lazy { BY_ID.apply(ordinal + 1) }

	override fun toString(): String = name.title()

	companion object {
		val BY_ID = EnumUtils.ordinalMapper<TextMode>()
	}
}