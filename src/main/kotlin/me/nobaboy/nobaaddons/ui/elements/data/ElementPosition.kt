package me.nobaboy.nobaaddons.ui.elements.data

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer

class ElementPosition(key: String = "") : ObjectProperty<ElementPosition>(key) {
	constructor(json: JsonObject) : this() {
		load(json)
	}

	// TODO change these to 0f..1f floats (or doubles) (this requires more work to migrate configs)
	var x by Property.of("x", 100)
	var y by Property.of("y", 100)
	var scale by Property.of("scale", Serializer.number(0.5f, 3f), 1f)

	fun copy() = ElementPosition(save() as JsonObject)
}