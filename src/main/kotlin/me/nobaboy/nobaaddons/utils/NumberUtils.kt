package me.nobaboy.nobaaddons.utils

import java.text.NumberFormat
import kotlin.math.pow
import kotlin.math.roundToInt

object NumberUtils {
	private val romanRegex = Regex("[IVXLCDM]+")
	private val romanValues = mapOf<Char, Int>(
		'M' to 1000,
		'D' to 500,
		'C' to 100,
		'L' to 50,
		'X' to 10,
		'V' to 5,
		'I' to 1
	)

	fun String.tryRomanToArabic() = toIntOrNull() ?: run {
		if(!romanRegex.matches(this)) null else romanToArabic()
	}

	fun String.romanToArabic(): Int {
		var number = 0
		var lastValue = 0

		this.uppercase().reversed().forEach { char ->
			val currentValue = romanValues[char] ?: 0
			number += if(currentValue < lastValue) -currentValue else currentValue
			lastValue = currentValue
		}

		return number
	}

	/**
	 * This code was taken and unmodified under CC BY-SA 3.0 license
	 * @link https://stackoverflow.com/a/22186845
	 * @author jpdymond
	 */
	fun Double.roundTo(precision: Int): Double {
		val scale = 10.0.pow(precision)
		return (this * scale).roundToInt() / scale
	}

	fun Float.roundTo(precision: Int): Float = toDouble().roundTo(precision).toFloat()

	fun Number.ordinalSuffix(): String {
		val long = this.toLong()
		if(long % 100 in 11..13) return "th"
		return when(long % 10) {
			1L -> "st"
			2L -> "nd"
			3L -> "rd"
			else -> "th"
		}
	}

	fun Number.addSeparators(): String = NumberFormat.getNumberInstance().format(this)
	fun Number.toShortString(): String = NumberFormat.getCompactNumberInstance().format(this)

	fun String.parseDoubleOrNull(): Double? {
		var text = lowercase().replace(",", "")

		val multiplier = when {
			text.endsWith("k") -> {
				text = text.substring(0, text.length - 1)
				1_000.0
			}
			text.endsWith("m") -> {
				text = text.substring(0, text.length - 1)
				1.million
			}
			text.endsWith("b") -> {
				text = text.substring(0, text.length - 1)
				1.billion
			}
			else -> 1.0
		}

		return text.toDoubleOrNull()?.let { it * multiplier }
	}

	fun String.parseDouble(): Double =
		parseDoubleOrNull() ?: throw NumberFormatException("formatDouble failed for '$this'")

	fun String.parseLong(): Long =
		parseDoubleOrNull()?.toLong() ?: throw NumberFormatException("formatLong failed for '$this'")

	fun String.parseInt(): Int =
		parseDoubleOrNull()?.toInt() ?: throw NumberFormatException("formatInt failed for '$this'")

	val Int.million get() = this * 1_000_000.0
	val Double.million get() = (this * 1_000_000.0).toLong()
	val Int.billion get() = this * 1_000_000_000.0
}