package me.nobaboy.nobaaddons.utils.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

object ColorKSerializer : KSerializer<Color> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.awt.Color", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: Color) {
		encoder.encodeString("${value.red}:${value.green}:${value.blue}:${value.alpha}")
	}

	override fun deserialize(decoder: Decoder): Color {
		val deserialized = decoder.decodeString()
		val parts = deserialized.split(':').map { it.toInt() }
		return when(parts.size) {
			// [r, g, b]
			3 -> Color(parts[0], parts[1], parts[2])
			// [r, g, b, a]
			4 -> Color(parts[0], parts[1], parts[2], parts[3])
			else -> throw IllegalArgumentException("Expected either 3 or 4 integer parts, got ${parts.size}")
		}
	}
}