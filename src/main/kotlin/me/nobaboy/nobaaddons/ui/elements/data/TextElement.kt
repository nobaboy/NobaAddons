package me.nobaboy.nobaaddons.ui.elements.data

import com.google.gson.JsonObject

class TextElement(key: String = "") : AbstractTextElement<TextElement>(key) {
	constructor(json: JsonObject) : this() {
		load(json)
	}

	fun copy() = TextElement(save() as JsonObject)
}