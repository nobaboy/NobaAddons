@file:Suppress("FunctionName", "UNCHECKED_CAST")

package me.nobaboy.nobaaddons.features.ui.infobox

import dev.celestialfault.histoire.migrations.Migrations
import kotlinx.serialization.json.JsonPrimitive
import me.nobaboy.nobaaddons.config.util.JsonList
import me.nobaboy.nobaaddons.config.util.JsonMap
import me.nobaboy.nobaaddons.config.util.getList
import me.nobaboy.nobaaddons.config.util.mapAndRename
import me.nobaboy.nobaaddons.config.util.moveTo
import me.nobaboy.nobaaddons.config.util.rename
import kotlin.collections.set

internal val migrations = Migrations("configVersion") {
	add(::`001_uiElementRefactor`)
	add(::`002_histoireMigration`)
}

private fun `001_uiElementRefactor`(json: JsonMap) {
	val infoBoxes = json.getList("infoboxes")

	infoBoxes.forEach { infoBox ->
		infoBox as JsonMap

		val mode = (infoBox["textMode"] as? JsonPrimitive)?.takeIf { it.isString }?.content
		if(mode == "PURE") infoBox["textMode"] = JsonPrimitive("NONE")

		val element = infoBox.remove("element") as JsonMap // pop element to rename later
		element.remove("identifier")
		element.moveTo("color", infoBox)
		// reset positioning to top left corner to account for change from pixels to a 0..1 double range
		element["x"] = JsonPrimitive(0.0)
		element["y"] = JsonPrimitive(0.0)

		infoBox["position"] = element // finally, rename element -> position
	}
}

private fun `002_histoireMigration`(json: JsonMap) {
	json.mapAndRename("infoboxes", "infoBoxes") {
		(it as JsonList).forEach { infoBox -> (infoBox as JsonMap).rename("textMode", "textShadow") }
	}
}
