package me.nobaboy.nobaaddons.utils.serializers

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color
import java.util.EnumMap

object ExtraSerializers {
	inline fun <reified K : Enum<K>, V> Serializer.Companion.enumMap(valueSerializer: Serializer<V>): Serializer<EnumMap<K, V>> =
		EnumMapSerializer(K::class.java, valueSerializer)

	inline fun <reified K : Enum<K>, reified V> Serializer.Companion.enumMap(): Serializer<EnumMap<K, V>> =
		enumMap(findSerializer<V>())

	val Serializer.Companion.color get() = object : Serializer<NobaColor> {
		override fun serialize(value: NobaColor): JsonElement = JsonPrimitive(value.rgb)

		override fun deserialize(element: JsonElement): NobaColor? =
			if(element is JsonPrimitive && element.isNumber) NobaColor(element.asInt) else null
	}
}