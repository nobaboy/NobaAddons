package me.nobaboy.nobaaddons.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class CooldownManager {
	private var markedAt: Timestamp = Timestamp.distantPast()
	private var cooldownDuration: Duration = 3.seconds

	fun startCooldown() {
		this.markedAt = Timestamp.currentTime()
	}

	fun startCooldown(duration: Duration) {
		this.startCooldown()
		this.cooldownDuration = duration
	}

	fun isOnCooldown(): Boolean {
		return markedAt.elapsedSince() < cooldownDuration
	}
}