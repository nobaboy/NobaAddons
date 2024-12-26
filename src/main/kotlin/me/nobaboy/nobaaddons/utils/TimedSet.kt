package me.nobaboy.nobaaddons.utils

import com.google.common.cache.RemovalCause
import com.google.common.collect.ForwardingSet
import kotlin.time.Duration

class TimedSet<T : Any>(
	duration: Duration,
	removalListener: (T, RemovalCause) -> Unit = { _, _ -> }
) : ForwardingSet<T>() {
	private val cache = TimedCache<T, Unit>(duration) { key, _, cause ->
		key?.let { removalListener(it, cause) }
	}

	fun toSet(): Set<T> = cache.keys.toSet()

	override fun isEmpty(): Boolean = cache.isEmpty()
	override fun toString(): String = "TimedSet(size=${cache.size}, elements=${toSet()}"
	override fun delegate(): Set<T> = cache.keys
}