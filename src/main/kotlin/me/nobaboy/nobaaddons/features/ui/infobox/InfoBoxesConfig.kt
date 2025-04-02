package me.nobaboy.nobaaddons.features.ui.infobox

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.migrations.Migrations
import kotlinx.serialization.json.JsonPrimitive
import me.nobaboy.nobaaddons.NobaAddons

@Suppress("UNCHECKED_CAST")
private val migrations = Migrations("configVersion") {
	add {
		val infoBoxes = it["infoboxes"] as? MutableList<Any> ?: return@add
		for(box in infoBoxes) {
			val box = box as MutableMap<String, Any>
			val mode = box["textMode"].let { it as? JsonPrimitive }?.takeIf { it.isString }?.content
			if(mode == "PURE") box.put("textMode", JsonPrimitive("NONE"))
			val position = box.remove("element") as MutableMap<String, Any> // pop element to rename later
			position.remove("identifier") // remove identifier
			// reset positioning to top left corner to account for change from pixels to a 0..1 double range
			position.put("x", JsonPrimitive(0.0))
			position.put("y", JsonPrimitive(0.0))
			position.remove("color")?.let { box.put("color", it) }
			box.put("position", position) // finally, rename element -> position
		}
	}

	add { it["infoBoxes"] = it.remove("infoboxes") ?: return@add }
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