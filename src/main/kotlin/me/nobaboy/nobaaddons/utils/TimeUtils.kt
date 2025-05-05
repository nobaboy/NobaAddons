package me.nobaboy.nobaaddons.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch
import me.nobaboy.nobaaddons.utils.TimeUtils.toShortString
import kotlin.text.replace
import kotlin.text.toLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

private val timeRegex = Regex("([\\d,]+)([hms])")
private val durations: Map<String, (Long) -> Duration> = mapOf(
	"h" to { it.hours },
	"m" to { it.minutes },
	"s" to { it.seconds },
)

object TimeUtils {
	// grumble grumble Instant.Companion.now() being error-level deprecated grumble grumble
	fun Instant.Companion.current() = Clock.System.now()

	fun String.asInstantOrNull(): Instant? {
		var time: Duration = 0.seconds

		timeRegex.forEachMatch(this) {
			time += durations[groups[2]!!.value]!!(groups[1]!!.value.replace(",", "").toLong())
		}

		return if(time > 0.seconds) Instant.current() + time else null
	}

	fun Instant.elapsedSince() = Instant.current() - this
	fun Instant.timeRemaining() = -elapsedSince()

	fun Instant.isPast(): Boolean = timeRemaining().isNegative()
	fun Instant.isFuture(): Boolean = timeRemaining().isPositive()

	fun Duration.toShortString(): String = buildList<String> {
		val duration = this@toShortString

		if(duration.isNegative()) {
			add("Soon!")
			return@buildList
		}

		val days = duration.inWholeDays.days
		val hours = duration.inWholeHours.hours - days
		val minutes = duration.inWholeMinutes.minutes - hours - days
		val seconds = duration.inWholeSeconds.seconds - minutes - hours - days

		if(days >= 1.days) add(hours.toString(DurationUnit.DAYS, 0))
		if(hours >= 1.hours) add(hours.toString(DurationUnit.HOURS, 0))
		if(minutes >= 1.minutes) add(minutes.toString(DurationUnit.MINUTES, 0))
		if(seconds >= 1.seconds || duration < 1.seconds) add(seconds.toString(DurationUnit.SECONDS, 0))
	}.joinToString(" ")
}