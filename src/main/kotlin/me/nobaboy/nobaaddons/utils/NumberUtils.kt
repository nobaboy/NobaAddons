package me.nobaboy.nobaaddons.utils

object NumberUtils {
	fun String.romanToDecimal(): Int {
		var decimal = 0
		var lastNumber = 0
		val romanNumeral = this.uppercase()
		for(x in romanNumeral.length - 1 downTo 0) {
			when(romanNumeral[x]) {
				'M' -> {
					decimal = processDecimal(1000, lastNumber, decimal)
					lastNumber = 1000
				}

				'D' -> {
					decimal = processDecimal(500, lastNumber, decimal)
					lastNumber = 500
				}

				'C' -> {
					decimal = processDecimal(100, lastNumber, decimal)
					lastNumber = 100
				}

				'L' -> {
					decimal = processDecimal(50, lastNumber, decimal)
					lastNumber = 50
				}

				'X' -> {
					decimal = processDecimal(10, lastNumber, decimal)
					lastNumber = 10
				}

				'V' -> {
					decimal = processDecimal(5, lastNumber, decimal)
					lastNumber = 5
				}

				'I' -> {
					decimal = processDecimal(1, lastNumber, decimal)
					lastNumber = 1
				}
			}
		}
		return decimal
	}

	private fun processDecimal(decimal: Int, lastNumber: Int, lastDecimal: Int) = if(lastNumber > decimal) {
		lastDecimal - decimal
	} else {
		lastDecimal + decimal
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

	fun Double.round(decimals: Int): Double {
		var multiplier = 1.0
		repeat(decimals) { multiplier *= 10 }
		val result = kotlin.math.round(this * multiplier) / multiplier
		val a = result.toString()
		val b = toString()
		return if(a.length > b.length) this else result
	}

	val Int.million get() = this * 1_000_000.0
	private val Int.billion get() = this * 1_000_000_000.0
	val Double.million get() = (this * 1_000_000.0).toLong()
}