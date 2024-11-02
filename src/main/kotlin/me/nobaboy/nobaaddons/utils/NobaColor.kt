package me.nobaboy.nobaaddons.utils

import java.awt.Color

enum class NobaColor(val colorCode: Char, private val color: Color) {
	BLACK('0', Color(0, 0, 0)),
	DARK_BLUE('1', Color(0, 0, 170)),
	DARK_GREEN('2', Color(0, 170, 0)),
	DARK_AQUA('3', Color(0, 170, 170)),
	DARK_RED('4', Color(170, 0, 0)),
	DARK_PURPLE('5', Color(170, 0, 170)),
	GOLD('6', Color(255, 170, 0)),
	GRAY('7', Color(170, 170, 170)),
	DARK_GRAY('8', Color(85, 85, 85)),
	BLUE('9', Color(85, 85, 255)),
	GREEN('a', Color(85, 255, 85)),
	AQUA('b', Color(85, 255, 255)),
	RED('c', Color(255, 85, 85)),
	LIGHT_PURPLE('d', Color(255, 85, 255)),
	YELLOW('e', Color(255, 255, 85)),
	WHITE('f', Color(255, 255, 255));

	val next by lazy {
		when(this) {
			WHITE -> BLACK
			else -> {
				val index = entries.indexOf(this)
				entries[index + 1]
			}
		}
	}

	fun toColor(): Color = color
}