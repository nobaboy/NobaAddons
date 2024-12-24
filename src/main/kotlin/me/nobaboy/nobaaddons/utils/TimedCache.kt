package me.nobaboy.nobaaddons.utils

import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalCause
import com.google.common.collect.ForwardingMap
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

class TimedCache<K : Any, V : Any>(
	duration: Duration,
	removalListener: (K?, V?, RemovalCause) -> Unit = { _, _, _ -> }
) : ForwardingMap<K, V>() {
	private val cache = CacheBuilder.newBuilder()
		.expireAfterWrite(duration.inWholeMilliseconds, TimeUnit.MILLISECONDS)
		.removalListener { removalListener(it.key, it.value, it.cause) }
		.build<K, V>()

	override fun delegate(): Map<K?, V?> = this.cache.asMap()
	override fun toString(): String = "TimedCache(size=$size, entries=${cache.asMap()})"
}