// This file intentionally violates this style rule to make the migration application order unquestionably clear.
@file:Suppress("FunctionName")

package me.nobaboy.nobaaddons.config

import com.google.gson.JsonObject

internal fun `001_removeYaclVersion`(json: JsonObject) {
	json.remove("version")
}

internal fun `002_inventoryCategory`(json: JsonObject) {
	json["uiAndVisuals"]?.asJsonObject?.let { uiAndVisuals ->
		val inventory = json["inventory"]?.asJsonObject ?: JsonObject().also {
			json.add("inventory", it)
		}

		uiAndVisuals.remove("slotInfo")?.asJsonObject?.let { slotInfo ->
			slotInfo.remove("enabled")
			inventory.add("slotInfo", slotInfo)
		}

		uiAndVisuals.remove("enchantments")?.asJsonObject?.let { enchantments ->
			enchantments.remove("parseItemEnchants")?.asBoolean?.let { parseItemEnchants ->
				enchantments.addProperty("modifyTooltips", parseItemEnchants)
			}

			inventory.add("enchantmentTooltips", enchantments)
		}
	}
}

internal fun `003_renameGlaciteMineshaftShareCorpses`(json: JsonObject) {
	val glaciteMineshaft = json["mining"]?.asJsonObject["glaciteMineshaft"]?.asJsonObject ?: return
	glaciteMineshaft.add("autoShareCorpses", glaciteMineshaft.remove("autoShareCorpseCoords") ?: return)
}