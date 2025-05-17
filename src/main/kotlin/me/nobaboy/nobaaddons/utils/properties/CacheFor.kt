package me.nobaboy.nobaaddons.utils.properties

import kotlinx.datetime.Instant
import me.nobaboy.nobaaddons.utils.TimeUtils.elapsedSince
import me.nobaboy.nobaaddons.utils.TimeUtils.now
import kotlin.reflect.KProperty
import kotlin.time.Duration

class CacheFor<T : Any?>(val duration: Duration, val getter: () -> T) {
	private val lock = Any()

	@Volatile private var value: T? = null
	@Volatile private var lastCached: Instant = Instant.DISTANT_PAST

	@Suppress("UNCHECKED_CAST", "unused")
	operator fun getValue(instance: Any, property: KProperty<*>): T {
		synchronized(lock) {
			if(lastCached.elapsedSince() <= duration) return value as T
			value = getter()
			lastCached = Instant.now
			return value as T
		}
	}
}