package me.nobaboy.nobaaddons.utils

import kotlinx.datetime.Instant
import me.nobaboy.nobaaddons.utils.NumberUtils.ordinalSuffix
import me.nobaboy.nobaaddons.utils.TimeUtils.current

/**
 * This is taken and adapted from SkyHanni, which is licensed under the LGPL-2.1.
 *
 * [Original source](https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/utils/SkyBlockTime.kt)
 */
data class SkyBlockTime(
	val year: Int = 1,
	val month: Int = 1,
	val day: Int = 1,
	val hour: Int = 0,
	val minute: Int = 0,
	val second: Int = 0,
) {
	val monthName get() = getMonthName(month)
	val dayName get() = "$day${day.ordinalSuffix()}"

	fun toInstantOrNull(): Instant? = Instant.fromEpochMilliseconds(toMillis())
	fun toInstant(): Instant = toInstantOrNull() ?: Instant.DISTANT_PAST

	fun toMillis(): Long = getSkyBlockTimeMillis(year, month, day, hour, minute, second) + SKYBLOCK_EPOCH_START_MILLIS

	companion object {
		private const val SKYBLOCK_EPOCH_START_MILLIS = 1559829300000L // Day 1, Year 1

		// SkyBlock Time Constants
		const val SKYBLOCK_YEAR_MILLIS = 124 * 60 * 60 * 1000L
		const val SKYBLOCK_SEASON_MILLIS = SKYBLOCK_YEAR_MILLIS / 4
		const val SKYBLOCK_MONTH_MILLIS = SKYBLOCK_YEAR_MILLIS / 12
		const val SKYBLOCK_DAY_MILLIS = SKYBLOCK_MONTH_MILLIS / 31
		const val SKYBLOCK_HOUR_MILLIS = SKYBLOCK_DAY_MILLIS / 24
		const val SKYBLOCK_MINUTE_MILLIS = SKYBLOCK_HOUR_MILLIS / 60
		const val SKYBLOCK_SECOND_MILLIS = SKYBLOCK_MINUTE_MILLIS / 60

		fun fromInstant(instant: Instant): SkyBlockTime =
			calculateSkyBlockTime(instant.toEpochMilliseconds() - SKYBLOCK_EPOCH_START_MILLIS)

		fun fromYear(year: Int): SkyBlockTime =
			fromInstant(Instant.fromEpochMilliseconds(SKYBLOCK_EPOCH_START_MILLIS + (SKYBLOCK_YEAR_MILLIS * year)))

		fun now(): SkyBlockTime = fromInstant(Instant.current())

		private fun calculateSkyBlockTime(milliseconds: Long): SkyBlockTime {
			var remainingMillis = milliseconds
			val year = (remainingMillis / SKYBLOCK_YEAR_MILLIS).toInt().also { remainingMillis %= SKYBLOCK_YEAR_MILLIS }
			val month = ((remainingMillis / SKYBLOCK_MONTH_MILLIS) + 1).toInt().also { remainingMillis %= SKYBLOCK_MONTH_MILLIS }
			val day = ((remainingMillis / SKYBLOCK_DAY_MILLIS) + 1).toInt().also { remainingMillis %= SKYBLOCK_DAY_MILLIS }
			val hour = (remainingMillis / SKYBLOCK_HOUR_MILLIS).toInt().also { remainingMillis %= SKYBLOCK_HOUR_MILLIS }
			val minute = (remainingMillis / SKYBLOCK_MINUTE_MILLIS).toInt().also { remainingMillis %= SKYBLOCK_MINUTE_MILLIS }
			val second = (remainingMillis / SKYBLOCK_SECOND_MILLIS).toInt()
			return SkyBlockTime(year, month, day, hour, minute, second)
		}

		private fun getSkyBlockTimeMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Long =
			year * SKYBLOCK_YEAR_MILLIS +
			(month - 1) * SKYBLOCK_MONTH_MILLIS +
			(day - 1) * SKYBLOCK_DAY_MILLIS +
			hour * SKYBLOCK_HOUR_MILLIS +
			minute * SKYBLOCK_MINUTE_MILLIS +
			second * SKYBLOCK_SECOND_MILLIS

		fun getMonthName(month: Int): String {
			val prefix = when((month - 1) % 3) {
				0 -> "Early "
				1 -> ""
				2 -> "Late "
				else -> "Undefined!"
			}

			val season = when((month - 1) / 3) {
				0 -> "Spring"
				1 -> "Summer"
				2 -> "Autumn"
				3 -> "Winter"
				else -> "Undefined!"
			}

			return prefix + season
		}
	}
}