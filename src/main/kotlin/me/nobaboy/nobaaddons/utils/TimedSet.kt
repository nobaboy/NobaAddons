package me.nobaboy.nobaaddons.utils

import com.google.common.cache.RemovalCause
import kotlin.time.Duration

class TimedSet<T : Any>(
	duration: Duration,
	private val removalListener: (T, RemovalCause) -> Unit = { _, _ -> }
) : Iterable<T> {
	private val cache = TimedCache<T, Unit>(duration) { key, _, cause ->
		key?.let { removalListener(it, cause) }
	}

	operator fun plusAssign(element: T) = add(element)
	operator fun minusAssign(element: T) = remove(element)

	fun add(element: T) {
		cache[element] = Unit
	}

	fun remove(element: T) {
		cache.remove(element)
	}

	operator fun contains(element: T): Boolean = element in cache

	fun clear() {
		cache.clear()
	}

	fun toSet(): Set<T> = cache.keys

	override fun iterator(): Iterator<T> = toSet().iterator()

	override fun toString(): String = "TimedSet(size=${cache.size}, elements=${toSet()}"
}