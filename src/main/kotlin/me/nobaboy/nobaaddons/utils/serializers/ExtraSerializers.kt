package me.nobaboy.nobaaddons.utils.serializers

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.Timestamp
import java.awt.Color
import java.util.EnumMap
import java.util.UUID

object ExtraSerializers {
	inline fun <reified K : Enum<K>, V> Serializer.Companion.enumMap(valueSerializer: Serializer<V>): Serializer<EnumMap<K, V>> =
		EnumMapSerializer(K::class.java, valueSerializer)

	inline fun <reified K : Enum<K>, reified V> Serializer.Companion.enumMap(): Serializer<EnumMap<K, V>> =
		enumMap(findSerializer<V>())

	val Serializer.Companion.color get() = object : Serializer<NobaColor> {
		override fun serialize(value: NobaColor): JsonElement = JsonPrimitive(value.rgb)

		override fun deserialize(element: JsonElement): NobaColor? =
			element.let { it as? JsonPrimitive }?.takeIf { it.isNumber }?.let { NobaColor(it.asInt) }
	}

	val Serializer.Companion.awtColor get() = object : Serializer<Color> {
		override fun serialize(value: Color): JsonElement =
			JsonPrimitive("${value.red}:${value.green}:${value.blue}:${value.alpha}")

		override fun deserialize(element: JsonElement): Color? {
			val deserialized = element.let { it as? JsonPrimitive }?.takeIf { it.isString }?.asString ?: return null
			val parts = deserialized.split(':').mapNotNull { it.toIntOrNull() }
			return when(parts.size) {
				// [r, g, b]
				3 -> Color(parts[0], parts[1], parts[2])
				// [r, g, b, a]
				4 -> Color(parts[0], parts[1], parts[2], parts[3])
				else -> null
			}
		}
	}

	val Serializer.Companion.timestamp get() = object : Serializer<Timestamp> {
		override fun serialize(value: Timestamp): JsonElement = JsonPrimitive(value.toMillis())

		override fun deserialize(element: JsonElement): Timestamp? =
			element.let { it as? JsonPrimitive }?.takeIf { it.isNumber }?.let { Timestamp(it.asLong) }
	}

	val Serializer.Companion.uuid get() = object : Serializer<UUID> {
		override fun serialize(value: UUID): JsonElement = JsonPrimitive(value.toString())

		override fun deserialize(element: JsonElement): UUID? =
			element
				.let { it as? JsonPrimitive }
				?.takeIf { it.isString }
				?.let { runCatching { UUID.fromString(it.asString) }.getOrNull() }
	}
}