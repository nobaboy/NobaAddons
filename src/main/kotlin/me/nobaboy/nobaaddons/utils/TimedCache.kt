package me.nobaboy.nobaaddons.utils

import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalCause
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

class TimedCache<K : Any, V : Any>(
	duration: Duration,
	private val removalListener: (K?, V?, RemovalCause) -> Unit = { _, _, _ -> }
) : Iterable<Map.Entry<K, V>> {
	private val cache = CacheBuilder.newBuilder()
		.expireAfterWrite(duration.inWholeMilliseconds, TimeUnit.MILLISECONDS)
		.removalListener { removalListener(it.key, it.value, it.cause) }
		.build<K, V>()

	val asReadOnlyMap: ConcurrentMap<K, V>
		get() = cache.asMap()

	val size: Int
		get() = asReadOnlyMap.size

	val values: Collection<V>
		get() = asReadOnlyMap.values

	val keys: Set<K>
		get() = asReadOnlyMap.keys

	fun getOrNull(key: K): V? = cache.getIfPresent(key)

	fun getOrPut(key: K, defaultValue: () -> V): V = getOrNull(key) ?: defaultValue().also { set(key, it) }

	operator fun set(key: K, value: V) = cache.put(key, value)

	operator fun contains(key: K): Boolean = cache.getIfPresent(key) != null

	fun remove(key: K) = cache.invalidate(key)

	fun clear() = cache.invalidateAll()

	override fun iterator(): Iterator<Map.Entry<K, V>> = asReadOnlyMap.iterator()

	override fun toString(): String = "TimedCache(size=$size, entries=$asReadOnlyMap)"
}