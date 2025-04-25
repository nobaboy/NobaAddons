package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import net.minecraft.util.Formatting

object StringUtils {
	private val TRAILING_ZERO = Regex("\\.0+(.)$")

	fun String.startsWith(list: List<String>): Boolean = list.any { this.startsWith(it) }

	fun String.title(): String = lowercase().split(" ").joinToString(" ") {
		if(it == "of" || it == "the") it
		else it.replaceFirstChar(Char::uppercase)
	}.replaceFirstChar(Char::uppercase) // ensure that the first character is always uppercase, even if the string starts with 'the' or 'of'

	fun String.cleanFormatting(): String = Formatting.strip(this)!!

	fun randomAlphanumeric(length: Int = 8): String {
		val allowedChars = ('0'..'9') + ('A'..'Z') + ('a'..'z')
		return (1..length)
			.map { allowedChars.random() }
			.joinToString("")
	}

	fun Double.toAbbreviatedString(thousandPrecision: Int = 1, millionPrecision: Int = 2, billionPrecision: Int = 1): String {
		return when {
			this >= 1_000_000_000 -> "${(this / 1_000_000_000.0).roundTo(billionPrecision)}b"
			this >= 1_000_000 -> "${(this / 1_000_000.0).roundTo(millionPrecision)}m"
			this >= 1_000 -> "${(this / 1_000.0).roundTo(thousandPrecision)}k"
			else -> this.toString()
		}.replace(TRAILING_ZERO, "$1")
	}

	fun Int.toAbbreviatedString(thousandPrecision: Int = 1, millionPrecision: Int = 2, billionPrecision: Int = 1): String =
		toDouble().toAbbreviatedString(thousandPrecision, millionPrecision, billionPrecision)
}