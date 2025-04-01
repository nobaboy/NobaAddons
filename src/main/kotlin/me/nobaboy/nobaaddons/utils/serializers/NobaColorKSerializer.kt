package me.nobaboy.nobaaddons.utils.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.nobaboy.nobaaddons.utils.NobaColor

object NobaColorKSerializer : KSerializer<NobaColor> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NobaColor", PrimitiveKind.INT)

	override fun serialize(encoder: Encoder, value: NobaColor) {
		encoder.encodeInt(value.rgb)
	}

	override fun deserialize(decoder: Decoder): NobaColor {
		return NobaColor(rgb = decoder.decodeInt())
	}
}