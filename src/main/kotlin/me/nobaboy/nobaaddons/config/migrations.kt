// This file intentionally violates this style rule to make the migration application order unquestionably clear.
@file:Suppress("FunctionName")

package me.nobaboy.nobaaddons.config

import com.google.gson.JsonObject

internal fun `001_removeYaclVersion`(json: JsonObject) {
	json.remove("version")
}

internal fun `002_inventoryCategory`(json: JsonObject) {
	val uiAndVisuals = json["uiAndVisuals"]?.asJsonObject ?: return
	val inventory = json["inventory"]?.asJsonObject ?: JsonObject().also { json.add("inventory", it) }

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

internal fun `003_renameGlaciteMineshaftShareCorpses`(json: JsonObject) {
	val glaciteMineshaft = json["mining"]?.asJsonObject["glaciteMineshaft"]?.asJsonObject ?: return
	glaciteMineshaft.add("autoShareCorpses", glaciteMineshaft.remove("autoShareCorpseCoords") ?: return)
}

internal fun `004_moveHideOtherPeopleFishing`(json: JsonObject) {
	val renderingTweaks = json["uiAndVisuals"]?.asJsonObject["renderingTweaks"]?.asJsonObject ?: return
	val fishing = json["fishing"]?.asJsonObject ?: JsonObject().also { json.add("fishing", it) }
	fishing.add("hideOtherPeopleFishing", renderingTweaks.remove("hideOtherPeopleFishing"))
}

internal fun `005_renameEtherwarpHelper`(json: JsonObject) {
	val uiAndVisuals = json["uiAndVisuals"]?.asJsonObject ?: return
	uiAndVisuals.add("etherwarpOverlay", uiAndVisuals.remove("etherwarpHelper"))
}