package me.nobaboy.nobaaddons.utils.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

object ColorKSerializer : KSerializer<Color> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: Color) {
		encoder.encodeString("${value.red}:${value.green}:${value.blue}:${value.alpha}")
	}

	override fun deserialize(decoder: Decoder): Color {
		val parts = decoder.decodeString().split(':').mapNotNull { it.toIntOrNull() }
		return when(parts.size) {
			3 -> Color(/* r = */ parts[0], /* g = */ parts[1], /* b = */ parts[2])
			4 -> Color(/* r = */ parts[0], /* g = */ parts[1], /* b = */ parts[2], /* a = */ parts[3])
			else -> throw SerializationException("Malformed color; expected either 3 or 4 integer parts, but got ${parts.size} instead")
		}
	}
}