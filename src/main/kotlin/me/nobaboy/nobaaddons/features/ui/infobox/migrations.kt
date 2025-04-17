@file:Suppress("FunctionName", "UNCHECKED_CAST")

package me.nobaboy.nobaaddons.features.ui.infobox

import dev.celestialfault.histoire.migrations.Migrations
import dev.celestialfault.histoire.migrations.MutableJsonList
import dev.celestialfault.histoire.migrations.MutableJsonMap
import dev.celestialfault.histoire.migrations.getList
import dev.celestialfault.histoire.migrations.rename
import kotlinx.serialization.json.JsonPrimitive
import me.nobaboy.nobaaddons.config.util.mapAndRename
import me.nobaboy.nobaaddons.config.util.moveTo
import kotlin.collections.set

internal val migrations = Migrations("configVersion") {
	add(::`001_uiElementRefactor`)
	add(::`002_histoireMigration`)
}

private fun `001_uiElementRefactor`(json: MutableJsonMap) {
	val infoBoxes = json.getList("infoboxes")

	infoBoxes.forEach { infoBox ->
		infoBox as MutableJsonMap

		val mode = (infoBox["textMode"] as? JsonPrimitive)?.takeIf { it.isString }?.content
		if(mode == "PURE") infoBox["textMode"] = JsonPrimitive("NONE")

		val element = infoBox.remove("element") as MutableJsonMap // pop element to rename later
		element.remove("identifier")
		element.moveTo("color", infoBox)
		// reset positioning to top left corner to account for change from pixels to a 0..1 double range
		element["x"] = JsonPrimitive(0.0)
		element["y"] = JsonPrimitive(0.0)

		infoBox["position"] = element // finally, rename element -> position
	}
}

private fun `002_histoireMigration`(json: MutableJsonMap) {
	json.mapAndRename("infoboxes", "infoBoxes") {
		(it as MutableJsonList).forEach { infoBox -> (infoBox as MutableJsonMap).rename("textMode", "textShadow") }
	}
}
