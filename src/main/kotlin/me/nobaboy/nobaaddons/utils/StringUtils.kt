package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import net.minecraft.util.Formatting

object StringUtils {
	infix fun String.lowercaseEquals(other: String) = this.equals(other, ignoreCase = true)
	infix fun String.lowercaseContains(other: String) = this.contains(other, ignoreCase = true)

	infix fun List<String>.lowercaseEquals(other: String) = this.any { it.equals(other, ignoreCase = true) }
	infix fun List<String>.lowercaseContains(other: String) = this.any { it.contains(other, ignoreCase = true) }

	fun String.startsWith(list: List<String>): Boolean = list.any { this.startsWith(it) }

	fun String.title(): String {
		return this.lowercase().split(" ").joinToString(" ") {
			if(it.contains("/")) {
				it.split("/").joinToString("/") { word ->
					word.replaceFirstChar { firstChar -> firstChar.uppercase() }
				}
			} else {
				it.replaceFirstChar { firstChar -> firstChar.uppercase() }
			}
		}
	}

	fun String.cleanFormatting(): String = Formatting.strip(this)!!

	fun randomAlphanumeric(length: Int = 8) : String {
		val allowedChars = ('0'..'9') + ('A'..'z') + ('a'..'z')
		return (1..length)
			.map { allowedChars.random() }
			.joinToString("")
	}

	fun Int.toAbbreviatedString(thousandPrecision: Int = 1, millionPrecision: Int = 2): String {
		return when {
			this.toInt() > 1_000 -> "${(this / 1_000.0).roundTo(thousandPrecision)}k"
			this.toInt() > 1_000_000 -> "${(this / 1_000_000.0).roundTo(millionPrecision)}m"
			else -> this.toString()
		}
	}
}