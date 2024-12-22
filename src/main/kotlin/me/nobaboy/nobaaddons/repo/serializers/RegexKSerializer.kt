package me.nobaboy.nobaaddons.repo.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object RegexKSerializer : KSerializer<Regex> {
	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("me.nobaboy.nobaaddons.repo.serializers.RegexKSerializer", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: Regex) {
		encoder.encodeString(value.pattern)
	}

	override fun deserialize(decoder: Decoder): Regex {
		return Regex(decoder.decodeString())
	}
}