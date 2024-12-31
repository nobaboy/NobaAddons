package me.nobaboy.nobaaddons.features.ui.infobox

import com.google.gson.JsonPrimitive
import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import dev.celestialfault.celestialconfig.migrations.Migrations
import me.nobaboy.nobaaddons.NobaAddons

private val migrations = Migrations.create {
	add {
		val infoBoxes = it["infoboxes"]?.asJsonArray ?: return@add
		for(box in infoBoxes) {
			val box = box.asJsonObject
			val mode = box["textMode"].let { it as? JsonPrimitive }?.takeIf { it.isString }?.asString
			if(mode == "PURE") box.addProperty("textMode", "NONE")
			val position = box.remove("element").asJsonObject // pop element to rename later
			position.remove("identifier") // remove identifier
			// reset positioning to top left corner to account for change from pixels to a 0..1 double range
			position.addProperty("x", 0.0)
			position.addProperty("y", 0.0)
			box.add("color", position.remove("color")) // element.color -> color
			box.add("position", position) // finally, rename element -> position
		}
	}
}

object InfoBoxesConfig : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("infoboxes.json"), migrations = migrations) {
	val infoBoxes by Property.of("infoboxes", Serializer.list(Serializer.obj<InfoBoxElement>()), mutableListOf())
}