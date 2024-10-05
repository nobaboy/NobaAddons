package me.nobaboy.nobaaddons.utils

import net.minecraft.util.Formatting

object StringUtils {
    infix fun String.lowercaseEquals(other: String) = this.equals(other, ignoreCase = true)
    infix fun String.lowercaseContains(other: String) = this.contains(other, ignoreCase = true)

    fun String.startsWith(list: List<String>): Boolean = list.any { this.startsWith(it) }

    fun String.capitalizeFirstLetters(): String {
        return this.split(" ").joinToString("") {
            if (it.contains("/")) {
                it.split("/").joinToString("/") { word ->
                    word.replaceFirstChar { firstChar -> firstChar.uppercase() }
                }
            } else {
                it.replaceFirstChar { firstChar -> firstChar.uppercase() }
            }
        }
    }

    fun String.clean(): String = Formatting.strip(this) ?: ""
}