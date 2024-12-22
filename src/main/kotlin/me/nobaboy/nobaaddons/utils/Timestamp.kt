package me.nobaboy.nobaaddons.utils

import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * This is taken and adapted from SkyHanni, which is licensed under the LGPL-2.1.
 *
 * [Original source](https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/utils/SkyBlockTime.kt)
 */
@JvmInline
@Serializable
value class Timestamp(private val millis: Long) : Comparable<Timestamp> {
	operator fun unaryMinus() = Timestamp(-millis)

	operator fun plus(duration: Duration) = Timestamp(millis + duration.inWholeMilliseconds)
	operator fun plus(milliseconds: Long) = Timestamp(millis + milliseconds)
	operator fun plus(other: Timestamp) = (millis + other.millis).milliseconds

	operator fun minus(duration: Duration) = Timestamp(millis - duration.inWholeMinutes)
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

	override fun compareTo(other: Timestamp): Int = millis.compareTo(other.millis)

	companion object {
		fun now() = Timestamp(System.currentTimeMillis())
		fun distantPast() = Timestamp(0)
		fun distantFuture() = Timestamp(Long.MAX_VALUE)

		fun Duration.fromNow() = now() + this

		fun Long.asTimestamp() = Timestamp(this)
		fun SkyBlockTime.asTimestamp() = Timestamp(toMillis())
		fun Instant.asTimestamp() = Timestamp(this.toEpochMilli())
	}
}