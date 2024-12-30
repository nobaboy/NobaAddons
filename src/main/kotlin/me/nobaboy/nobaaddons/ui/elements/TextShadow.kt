package me.nobaboy.nobaaddons.ui.elements

import me.nobaboy.nobaaddons.utils.EnumUtils
import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class TextShadow {
	NONE,
	SHADOW,
	OUTLINE;

	val next: TextShadow by lazy { BY_ID.apply(ordinal + 1) }

	override fun toString(): String = name.title()

	companion object {
		val BY_ID = EnumUtils.ordinalMapper<TextShadow>()
	}
}