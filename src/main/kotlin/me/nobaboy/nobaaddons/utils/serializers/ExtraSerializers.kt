package me.nobaboy.nobaaddons.utils.serializers

import dev.celestialfault.celestialconfig.Serializer
import java.util.EnumMap

object ExtraSerializers {
	inline fun <reified K : Enum<K>, V> Serializer.Companion.enumMap(valueSerializer: Serializer<V>): Serializer<EnumMap<K, V>> =
		EnumMapSerializer(K::class.java, valueSerializer)

	inline fun <reified K : Enum<K>, reified V> Serializer.Companion.enumMap(): Serializer<EnumMap<K, V>> =
		enumMap(findSerializer<V>())
}