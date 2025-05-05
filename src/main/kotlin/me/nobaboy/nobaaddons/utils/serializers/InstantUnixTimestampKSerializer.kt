package me.nobaboy.nobaaddons.utils.serializers

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InstantUnixTimestampKSerializer : KSerializer<Instant> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)

	override fun serialize(encoder: Encoder, value: Instant) {
		encoder.encodeLong(value.toEpochMilliseconds())
	}

	override fun deserialize(decoder: Decoder): Instant {
		return Instant.fromEpochMilliseconds(decoder.decodeLong())
	}
}