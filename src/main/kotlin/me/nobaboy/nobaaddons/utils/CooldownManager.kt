package me.nobaboy.nobaaddons.utils

import kotlinx.datetime.Instant
import me.nobaboy.nobaaddons.utils.TimeUtils.elapsedSince
import me.nobaboy.nobaaddons.utils.TimeUtils.now
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class CooldownManager(private val defaultDuration: Duration = 3.seconds) {
	private var markedAt: Instant = Instant.DISTANT_PAST
	private var cooldownDuration: Duration = defaultDuration

	fun startCooldown(duration: Duration = defaultDuration) {
		this.markedAt = Instant.now
		this.cooldownDuration = duration
	}

	fun isOnCooldown(): Boolean {
		return markedAt.elapsedSince() < cooldownDuration
	}
}