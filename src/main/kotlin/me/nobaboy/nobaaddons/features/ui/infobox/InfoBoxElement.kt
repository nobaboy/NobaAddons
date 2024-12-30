package me.nobaboy.nobaaddons.features.ui.infobox

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.Property
import me.nobaboy.nobaaddons.ui.elements.data.AbstractTextElement

class InfoBoxElement() : AbstractTextElement<InfoBoxElement>() {
	constructor(json: JsonObject) : this() {
		load(json)
	}

	var text by Property.of("text", "Info Box")

	fun copy() = InfoBoxElement(save() as JsonObject)
}