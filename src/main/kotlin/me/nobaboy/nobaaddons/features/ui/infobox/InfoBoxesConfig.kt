package me.nobaboy.nobaaddons.features.ui.infobox

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.migrations.Migrations
import kotlinx.serialization.json.JsonPrimitive
import me.nobaboy.nobaaddons.NobaAddons

private typealias JsonMap = MutableMap<String, Any>

@Suppress("UNCHECKED_CAST")
private val migrations = Migrations("configVersion") {
	add {
		val infoBoxes = it["infoboxes"] as? MutableList<Any> ?: return@add

		infoBoxes.forEach { infoBox ->
			val infoBox = infoBox as JsonMap

			val mode = infoBox["textMode"].let { it as? JsonPrimitive }?.takeIf { it.isString }?.content
			if(mode == "PURE") infoBox["textMode"] = JsonPrimitive("NONE")

			val element = infoBox.remove("element") as JsonMap // pop element to rename later
			element.remove("identifier")
			element.remove("color")?.let { infoBox["color"] = it }
			// reset positioning to top left corner to account for change from pixels to a 0..1 double range
			element["x"] = JsonPrimitive(0.0)
			element["y"] = JsonPrimitive(0.0)

			infoBox["position"] = element // finally, rename element -> position
		}
	}

	add {
		val infoBoxes = (it.remove("infoboxes") ?: return@add) as MutableList<Any>

		infoBoxes.forEach { infoBox ->
			val infoBox = infoBox as JsonMap
			infoBox.remove("textMode")?.let { infoBox["textShadow"] = it }
		}

		it["infoBoxes"] = infoBoxes
	}
}

object InfoBoxesConfig : Histoire(NobaAddons.CONFIG_DIR.resolve("infoboxes.json").toFile(), migrations = migrations) {
	var infoBoxes: MutableList<InfoBoxElement> = mutableListOf()

	// this cannot be private in any capacity (not even a `private set`), as reflection totally shits the bed
	// and behaves incredibly inconsistently when encountering a private var in an object class
	@Suppress("unused")
	var configVersion: Int = migrations.currentVersion

	override fun load() {
		super.load()
		InfoBoxesManager.recreateUIElements()
	}
}