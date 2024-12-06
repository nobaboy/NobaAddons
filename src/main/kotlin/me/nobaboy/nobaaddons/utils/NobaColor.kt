package me.nobaboy.nobaaddons.utils

import net.minecraft.util.Formatting
import net.minecraft.util.function.ValueLists
import java.awt.Color

enum class NobaColor(val colorCode: Char, private val color: Color, private val formatting: Formatting) {
	BLACK('0', Color(0, 0, 0), Formatting.BLACK),
	DARK_BLUE('1', Color(0, 0, 170), Formatting.DARK_BLUE),
	DARK_GREEN('2', Color(0, 170, 0), Formatting.DARK_GREEN),
	DARK_AQUA('3', Color(0, 170, 170), Formatting.DARK_AQUA),
	DARK_RED('4', Color(170, 0, 0), Formatting.DARK_RED),
	DARK_PURPLE('5', Color(170, 0, 170), Formatting.DARK_PURPLE),
	GOLD('6', Color(255, 170, 0), Formatting.GOLD),
	GRAY('7', Color(170, 170, 170), Formatting.GRAY),
	DARK_GRAY('8', Color(85, 85, 85), Formatting.DARK_GRAY),
	BLUE('9', Color(85, 85, 255), Formatting.BLUE),
	GREEN('a', Color(85, 255, 85), Formatting.GREEN),
	AQUA('b', Color(85, 255, 255), Formatting.AQUA),
	RED('c', Color(255, 85, 85), Formatting.RED),
	LIGHT_PURPLE('d', Color(255, 85, 255), Formatting.LIGHT_PURPLE),
	YELLOW('e', Color(255, 255, 85), Formatting.YELLOW),
	WHITE('f', Color(255, 255, 255), Formatting.WHITE);

	val next by lazy { BY_ID.apply(ordinal + 1) }
	val rgb get() = toColor().rgb

	fun toColor(): Color = color
	fun toFormatting(): Formatting = formatting

	companion object {
		val BY_ID = ValueLists.createIdToValueFunction(NobaColor::ordinal, entries.toTypedArray(), ValueLists.OutOfBoundsHandling.WRAP)
	}
}