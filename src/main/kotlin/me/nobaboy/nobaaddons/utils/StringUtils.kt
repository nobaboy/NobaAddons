package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch
import net.minecraft.util.Formatting
import kotlin.text.replace
import kotlin.text.toLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val TRAILING_ZERO = Regex("\\.0+")
private val timeRegex = Regex("([\\d,]+)([dhms])")
private val durations: Map<String, (Long) -> Duration> = mapOf(
	"d" to { it.days },
	"h" to { it.hours },
	"m" to { it.minutes },
	"s" to { it.seconds },
)

object StringUtils {
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

	@Suppress("KotlinConstantConditions")
	fun Double.toAbbreviatedString(
		thousandPrecision: Int = 1,
		millionPrecision: Int = 2,
		billionPrecision: Int = 1,
	): String {
		return when {
			this >= 1_000_000_000 -> "${(this / 1_000_000_000.0).roundTo(billionPrecision)}b"
			this >= 1_000_000 -> "${(this / 1_000_000.0).roundTo(millionPrecision)}m"
			this >= 1_000 -> "${(this / 1_000.0).roundTo(thousandPrecision)}k"
			else -> toString()
		}.replace(TRAILING_ZERO, "")
	}

	fun Int.toAbbreviatedString(
		thousandPrecision: Int = 1,
		millionPrecision: Int = 2,
		billionPrecision: Int = 1,
	): String =
		toDouble().toAbbreviatedString(thousandPrecision, millionPrecision, billionPrecision)

	// TODO this would make more sense to be in a TimeUtils class
	fun String.asDuration(): Duration? {
		var time: Duration = 0.seconds

		timeRegex.forEachMatch(this) {
			time += durations[groups[2]!!.value]!!(groups[1]!!.value.replace(",", "").toLong())
		}

		return time.takeIf { it > 0.seconds }
	}

	fun String.isCommaNumeric(): Boolean = all { it.isDigit() || it == ',' }
	fun String.isNumeric(): Boolean = all(Char::isDigit)
}