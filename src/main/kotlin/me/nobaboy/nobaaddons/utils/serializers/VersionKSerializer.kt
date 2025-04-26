package me.nobaboy.nobaaddons.utils.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.fabricmc.loader.api.Version

class VersionKSerializer : KSerializer<Version> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Version", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: Version) {
		encoder.encodeString(value.toString())
	}

	override fun deserialize(decoder: Decoder): Version = Version.parse(decoder.decodeString())
}