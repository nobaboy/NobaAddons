package me.nobaboy.nobaaddons.repo.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// I don't know if there's already a serializer for Regex, and I don't really feel like figuring that out
// when it's not all that difficult to just write one.
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