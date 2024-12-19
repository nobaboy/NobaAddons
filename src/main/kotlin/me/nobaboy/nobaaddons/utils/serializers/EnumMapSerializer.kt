package me.nobaboy.nobaaddons.utils.serializers

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.Serializer
import java.util.EnumMap

class EnumMapSerializer<K : Enum<K>, V>(
	private val enumClass: Class<K>,
	private val valueSerializer: Serializer<V>,
) : Serializer<EnumMap<K, V>> {
	private val values = enumClass.enumConstants

	override fun serialize(value: EnumMap<K, V>) = JsonObject().apply {
		value.forEach { k, v -> add(k.name, valueSerializer.serialize(v)) }
	}

	override fun deserialize(element: JsonElement): EnumMap<K, V>? {
		if(element !is JsonObject) return null

		val map = EnumMap<K, V>(enumClass)
		element.asMap().forEach { k, v ->
			val enum = values.firstOrNull { it.name == k } ?: return@forEach
			valueSerializer.deserialize(v)?.let { deserialized -> map.put(enum, deserialized) }
		}
		return map
	}
}