package me.nobaboy.nobaaddons.utils

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch
import me.nobaboy.nobaaddons.utils.hypixel.SkyBlockTime
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
@Deprecated("Use ktx Instant instead")
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

	fun toMillis(): Long = millis
	override fun toString(): String = Instant.ofEpochMilli(toMillis()).toString()

	override fun compareTo(other: Timestamp): Int = millis.compareTo(other.millis)

	companion object {
		@Deprecated(
			"Use ktx Instant instead",
			replaceWith = ReplaceWith(
				expression = "Instant.current()",
				imports = [
					"kotlinx.datetime.Instant",
					"me.nobaboy.nobaaddons.utils.TimeUtils.current"
				]
			)
		)
		fun now() = Timestamp(System.currentTimeMillis())

		@Deprecated(
			"Use ktx Instant instead",
			replaceWith = ReplaceWith(
				expression = "Instant.DISTANT_PAST",
				imports = ["kotlinx.datetime.Instant"]
			)
		)
		fun distantPast() = Timestamp(0)

		fun Duration.fromNow() = now() + this

		fun SkyBlockTime.asTimestamp() = Timestamp(toMillis())
		fun Instant.asTimestamp() = Timestamp(this.toEpochMilli())

		fun String.asTimestamp(): Timestamp? {
			var time: Duration = 0.seconds

			timeRegex.forEachMatch(this) {
				time += durations[groups[2]!!.value]!!(groups[1]!!.value.replace(",", "").toLong())
			}

			return if(time > 0.seconds) now() + time else null
		}
	}
}