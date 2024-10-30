package me.nobaboy.nobaaddons.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class CooldownManager(private val defaultDuration: Duration = 3.seconds) {
	private var markedAt: Timestamp = Timestamp.distantPast()
	private var cooldownDuration: Duration = defaultDuration

	fun startCooldown(duration: Duration = defaultDuration) {
		this.markedAt = Timestamp.currentTime()
		this.cooldownDuration = duration
	}

	fun isOnCooldown(): Boolean {
		return markedAt.elapsedSince() < cooldownDuration
	}
}