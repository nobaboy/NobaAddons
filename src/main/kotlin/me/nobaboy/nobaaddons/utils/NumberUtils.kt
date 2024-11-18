package me.nobaboy.nobaaddons.utils

import kotlin.math.pow

object NumberUtils {
	private val romanValues = mapOf<Char, Int>(
		'M' to 1000,
		'D' to 500,
		'C' to 100,
		'L' to 50,
		'X' to 10,
		'V' to 5,
		'I' to 1
	)

	fun String.tryRomanToArabic() = toIntOrNull() ?: romanToArabic()

	fun String.romanToArabic(): Int {
		var number = 0
		var lastNumber = 0

		this.uppercase().reversed().forEach { char ->
			val currentNumber = romanValues[char] ?: 0
			number += if(currentNumber < lastNumber) -currentNumber else currentNumber
			lastNumber = currentNumber
		}

		return number
	}

	private fun String.formatDoubleOrNull(): Double? {
		var text = lowercase().replace(",", "")

		val multiplier = if(text.endsWith("k")) {
			text = text.substring(0, text.length - 1)
			1_000.0
		} else if(text.endsWith("m")) {
			text = text.substring(0, text.length - 1)
			1.million
		} else if(text.endsWith("b")) {
			text = text.substring(0, text.length - 1)
			1.billion
		} else 1.0
		return text.toDoubleOrNull()?.let {
			it * multiplier
		}
	}

	fun String.formatDouble(): Double =
		formatDoubleOrNull() ?: throw NumberFormatException("formatDouble failed for '$this'")

	fun String.formatLong(): Long =
		formatDoubleOrNull()?.toLong() ?: throw NumberFormatException("formatLong failed for '$this'")

	fun String.formatInt(): Int =
		formatDoubleOrNull()?.toInt() ?: throw NumberFormatException("formatInt failed for '$this'")

	/**
	 * This code was taken and unmodified under CC BY-SA 3.0 license
	 * @link https://stackoverflow.com/a/22186845
	 * @author jpdymond
	 */
	fun Double.roundTo(precision: Int): Double {
		val scale = 10.0.pow(precision)
		return kotlin.math.round(this * scale) / scale
	}

	fun Float.roundTo(precision: Int): Float = toDouble().roundTo(precision).toFloat()

	val Int.million get() = this * 1_000_000.0
	private val Int.billion get() = this * 1_000_000_000.0
	val Double.million get() = (this * 1_000_000.0).toLong()
}