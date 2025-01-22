package me.nobaboy.nobaaddons.utils.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.nobaboy.nobaaddons.utils.NobaVec

object NobaVecKSerializer : KSerializer<NobaVec> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor: SerialDescriptor = SerialDescriptor("NobaVec", DoubleArraySerializer().descriptor)

	override fun serialize(encoder: Encoder, value: NobaVec) {
		val array = doubleArrayOf(value.x, value.y, value.z)
		encoder.encodeSerializableValue(DoubleArraySerializer(), array)
	}

	override fun deserialize(decoder: Decoder): NobaVec {
		val array = decoder.decodeSerializableValue(DoubleArraySerializer())
		require(array.size == 3) { "NobaVec requires 3 elements" }
		return NobaVec(array[0], array[1], array[2])
	}
}