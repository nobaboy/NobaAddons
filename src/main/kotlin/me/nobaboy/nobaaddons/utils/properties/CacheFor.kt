package me.nobaboy.nobaaddons.utils.properties

import me.nobaboy.nobaaddons.utils.Timestamp
import kotlin.reflect.KProperty
import kotlin.time.Duration

class CacheFor<T : Any?>(val duration: Duration, val getter: () -> T) {
	private val lock = Any()

	@Volatile private var value: T? = null
	@Volatile private var lastCached: Timestamp = Timestamp.distantPast()

	@Suppress("UNCHECKED_CAST")
	operator fun getValue(instance: Any, property: KProperty<*>): T {
		synchronized(lock) {
			if(lastCached.elapsedSince() <= duration) return value as T
			value = getter()
			lastCached = Timestamp.now()
			return value as T
		}
	}
}