package me.nobaboy.nobaaddons.utils

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

private val timeRegex = Regex("([\\d,]+)([hms])")
private val durations: Map<String, (Long) -> Duration> = mapOf(
	"h" to { it.hours },
	"m" to { it.minutes },
	"s" to { it.seconds },
)

/**
 * This is taken and adapted from SkyHanni, which is licensed under the LGPL-2.1.
 *
 * [Original source](https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/utils/SimpleTimeMark.kt)
 */
// TODO use kotlinx.datetime.Instant instead?
@JvmInline
@Serializable
value class Timestamp(private val millis: Long) : Comparable<Timestamp> {
	operator fun unaryMinus() = Timestamp(-millis)

	operator fun plus(duration: Duration) = Timestamp(millis + duration.inWholeMilliseconds)
	operator fun plus(milliseconds: Long) = Timestamp(millis + milliseconds)
	operator fun plus(other: Timestamp) = (millis + other.millis).milliseconds

	operator fun minus(duration: Duration) = Timestamp(millis - duration.inWholeMilliseconds)
	operator fun minus(milliseconds: Long) = Timestamp(millis - milliseconds)
	operator fun minus(other: Timestamp) = (millis - other.millis).milliseconds

	fun elapsedSince() = now() - this
	fun elapsedSeconds() = elapsedSince().inWholeSeconds
	fun elapsedMinutes() = elapsedSince().inWholeMinutes

	fun timeRemaining() = -elapsedSince()

	fun isPast(): Boolean = timeRemaining().isNegative()
	fun isFuture(): Boolean = timeRemaining().isPositive()
	fun isDistantPast(): Boolean = millis == 0L

	fun toMillis(): Long = millis
	override fun toString(): String = Instant.ofEpochMilli(toMillis()).toString()

	override fun compareTo(other: Timestamp): Int = millis.compareTo(other.millis)

	companion object {
		fun now() = Timestamp(System.currentTimeMillis())
		fun distantPast() = Timestamp(0)
		fun distantFuture() = Timestamp(Long.MAX_VALUE)

		fun Duration.fromNow() = now() + this

		fun Long.asTimestamp() = Timestamp(this)
		fun SkyBlockTime.asTimestamp() = Timestamp(toMillis())
		fun Instant.asTimestamp() = Timestamp(this.toEpochMilli())

		fun String.asTimestamp(): Timestamp? {
			var time: Duration = 0.seconds

			timeRegex.forEachMatch(this) {
				time += durations[groups[2]!!.value]!!(groups[1]!!.value.replace(",", "").toLong())
			}

			return if(time > 0.seconds) now() + time else null
		}

		// TODO this should be in a separate class (like a TimeUtils or similar), but I didn't want to make
		//      an entire extra class just for this one method
		fun Duration.toShortString(): String = buildList<String> {
			val duration = this@toShortString

			val days = duration.inWholeDays.days
			val hours = duration.inWholeHours.hours - days
			val minutes = duration.inWholeMinutes.minutes - hours - days
			val seconds = duration.inWholeSeconds.seconds - minutes - hours - days

			if(days >= 1.days) add(hours.toString(DurationUnit.DAYS, 0))
			if(hours >= 1.hours) add(hours.toString(DurationUnit.HOURS, 0))
			if(minutes >= 1.minutes) add(minutes.toString(DurationUnit.MINUTES, 0))
			if(seconds >= 1.seconds) add(seconds.toString(DurationUnit.SECONDS, 0))
		}.joinToString(" ")
	}
}