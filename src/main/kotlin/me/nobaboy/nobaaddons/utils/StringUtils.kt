package me.nobaboy.nobaaddons.utils

import net.minecraft.util.Formatting

object StringUtils {
	infix fun String.lowercaseEquals(other: String) = this.equals(other, ignoreCase = true)
	infix fun String.lowercaseContains(other: String) = this.contains(other, ignoreCase = true)
	infix fun String.lowercaseEquals(other: List<String>) = other.any { this.equals(it, ignoreCase = true) }
	infix fun String.lowercaseContains(other: List<String>) = other.any { this.contains(it, ignoreCase = true) }

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
}