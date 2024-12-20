package me.nobaboy.nobaaddons.repo.adapters

import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import me.nobaboy.nobaaddons.utils.NobaVec

// TODO none of this has actually been tested to work
class NobaVecAdapter : TypeAdapter<NobaVec>() {
	override fun write(writer: JsonWriter, value: NobaVec) {
		writer.beginArray()
		writer.value(value.x)
		writer.value(value.y)
		writer.value(value.z)
		writer.endArray()
	}

	override fun read(reader: JsonReader): NobaVec? {
		if(reader.peek() == JsonToken.NULL) {
			reader.nextNull()
			return null
		}

		if(reader.peek() != JsonToken.BEGIN_ARRAY) throw JsonParseException("Element must be an array")

		reader.beginArray()
		val items = buildList { while(reader.peek() != JsonToken.END_ARRAY) add(reader.nextDouble()) }
		reader.endArray()
		return fromList(items)
	}

	private fun fromList(list: List<Double>): NobaVec =
		when(list.size) {
			2 -> NobaVec(list[0], -1.0, list[1])
			3 -> NobaVec(list[0], list[1], list[2])
			else -> throw JsonParseException("Expected between 2 and 3 number values, got ${list.size} instead")
		}
}