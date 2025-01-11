package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import net.minecraft.util.Formatting

object StringUtils {
	private val TRAILING_ZERO = Regex("\\.0+(.)$")

	infix fun String.lowercaseEquals(other: String) = this.equals(other, ignoreCase = true)
	infix fun String.lowercaseContains(other: String) = this.contains(other, ignoreCase = true)

	infix fun List<String>.lowercaseEquals(other: String) = this.any { it.equals(other, ignoreCase = true) }
	infix fun List<String>.lowercaseContains(other: String) = this.any { it.contains(other, ignoreCase = true) }

	fun String.startsWith(list: List<String>): Boolean = list.any { this.startsWith(it) }

	fun String.title(): String = lowercase().split(" ").joinToString(" ") {
		if(it == "of" || it == "the") it
		else it.replaceFirstChar(Char::uppercase)
	}.replaceFirstChar(Char::uppercase) // ensure that the first character is always uppercase, even if the string starts with 'the' or 'of'

	fun String.cleanFormatting(): String = Formatting.strip(this)!!

	fun randomAlphanumeric(length: Int = 8): String {
		val allowedChars = ('0'..'9') + ('A'..'z') + ('a'..'z')
		return (1..length)
			.map { allowedChars.random() }
			.joinToString("")
	}

	fun Int.toAbbreviatedString(thousandPrecision: Int = 1, millionPrecision: Int = 2, billionPrecision: Int = 1): String {
		return when {
			this.toInt() >= 1_000_000_000 -> "${(this / 1_000_000_000.0).roundTo(billionPrecision)}b"
			this.toInt() >= 1_000_000 -> "${(this / 1_000_000.0).roundTo(millionPrecision)}m"
			this.toInt() >= 1_000 -> "${(this / 1_000.0).roundTo(thousandPrecision)}k"
			else -> this.toString()
		}.replace(TRAILING_ZERO, "$1")
	}

	fun String.stripWhitespace(): String = dropWhile(Char::isWhitespace).dropLastWhile(Char::isWhitespace)
}