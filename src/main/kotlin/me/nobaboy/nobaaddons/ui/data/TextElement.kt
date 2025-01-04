package me.nobaboy.nobaaddons.ui.data

import com.google.gson.JsonObject
import me.nobaboy.nobaaddons.ui.TextShadow

/**
 * Generic implementation of an [AbstractTextElement], suitable for basic text elements that don't require
 * any extra properties.
 *
 * ## Example
 *
 * ```kt
 * object Config : AbstractConfig(...) {
 *     val element by TextElement("element", color = 0xFF00FF)
 * }
 * ```
 */
class TextElement(key: String = "") : AbstractTextElement<TextElement>(key) {
	constructor(
		key: String = "",
		textShadow: TextShadow? = null,
		color: Int? = null,
		outlineColor: Int? = null,
		position: ElementPosition? = null,
	) : this(key) {
		textShadow?.let { this.textShadow = it }
		color?.let { this.color = it }
		outlineColor?.let { this.outlineColor = it }
		position?.let {
			this.position.x = it.x
			this.position.y = it.y
			this.position.scale = it.scale
		}
	}

	constructor(json: JsonObject) : this() {
		load(json)
	}

	fun copy() = TextElement(save() as JsonObject)
}