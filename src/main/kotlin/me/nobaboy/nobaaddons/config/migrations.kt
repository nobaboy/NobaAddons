package me.nobaboy.nobaaddons.config

import com.google.gson.JsonObject

fun `001_removeYaclVersion`(json: JsonObject) {
	json.remove("version")
}

fun `002_inventoryCategory`(json: JsonObject) {
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

fun `003_renameGlaciteMineshaftShareCorpses`(json: JsonObject) {
	json["mining"]?.asJsonObject?.let { mining ->
		mining["glaciteMineshaft"]?.asJsonObject?.let { glaciteMineshaft ->
			glaciteMineshaft.remove("autoShareCorpseCoords")?.asBoolean?.let { autoShareCorpseCoords ->
				glaciteMineshaft.addProperty("autoShareCorpses", autoShareCorpseCoords)
			}
		}
	}
}