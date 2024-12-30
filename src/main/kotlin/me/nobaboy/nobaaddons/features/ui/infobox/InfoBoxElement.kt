package me.nobaboy.nobaaddons.features.ui.infobox

import com.google.gson.JsonObject
import dev.celestialfault.celestialconfig.Property
import me.nobaboy.nobaaddons.ui.data.AbstractTextElement
import me.nobaboy.nobaaddons.ui.data.ElementPosition

class InfoBoxElement() : AbstractTextElement<InfoBoxElement>() {
	constructor(position: ElementPosition) : this() {
		this.position.x = position.x
		this.position.y = position.y
		this.position.scale = position.scale
	}

	constructor(json: JsonObject) : this() {
		load(json)
	}

	var text by Property.of("text", "")

	fun copy() = InfoBoxElement(save() as JsonObject)
}