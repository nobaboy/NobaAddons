package me.nobaboy.nobaaddons.ui.data

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer

/**
 * Basic HUD element positioning data used by [me.nobaboy.nobaaddons.ui.HudElement]
 */
class ElementPosition(key: String = "") : ObjectProperty<ElementPosition>(key) {
	constructor(key: String = "", x: Double = 0.0, y: Double = 0.0, scale: Float = 1f) : this(key) {
		this.x = x
		this.y = y
		this.scale = scale
	}

	constructor(json: JsonObject) : this() {
		load(json)
	}

	var x by Property.of("x", Serializer.number(0.0, 1.0), 0.0)
	var y by Property.of("y", Serializer.number(0.0, 1.0), 0.0)
	var scale by Property.of("scale", Serializer.number(0.5f, 3f), 1f)

	fun copy() = ElementPosition(save() as JsonObject)
}