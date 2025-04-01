// This file intentionally violates this style rule to make the migration application order unquestionably clear.
@file:Suppress("FunctionName", "UNCHECKED_CAST")

package me.nobaboy.nobaaddons.config

import kotlinx.serialization.json.JsonPrimitive

internal typealias JsonMap = MutableMap<String, Any>

internal fun `001_removeYaclVersion`(json: JsonMap) {
	json.remove("version")
}

internal fun `002_inventoryCategory`(json: JsonMap) {
	val uiAndVisuals = json["uiAndVisuals"] as? JsonMap ?: return
	val inventory = json.getOrPut("inventory") { mutableMapOf<String, Any>() } as JsonMap

	uiAndVisuals.remove("slotInfo")?.let { it as? JsonMap }?.let { slotInfo ->
		slotInfo.remove("enabled")
		inventory.put("slotInfo", slotInfo)
	}

	uiAndVisuals.remove("enchantments")?.let { it as? JsonMap }?.let { enchantments ->
		enchantments.remove("parseItemEnchants")?.let { it as? JsonPrimitive }?.let { parseItemEnchants ->
			enchantments.put("modifyTooltips", parseItemEnchants)
		}

		inventory.put("enchantmentTooltips", enchantments)
	}
}

internal fun `003_renameGlaciteMineshaftShareCorpses`(json: JsonMap) {
	val glaciteMineshaft = json["mining"]?.let { it as? JsonMap }?.get("glaciteMineshaft") as? JsonMap ?: return
	glaciteMineshaft.put("autoShareCorpses", glaciteMineshaft.remove("autoShareCorpseCoords") ?: return)
}

internal fun `004_moveHideOtherPeopleFishing`(json: JsonMap) {
	val renderingTweaks = json["uiAndVisuals"]?.let { it as? JsonMap }?.get("renderingTweaks") as? JsonMap ?: return
	val fishing = json.getOrPut("fishing") { mutableMapOf<String, Any>() } as JsonMap
	fishing.put("hideOtherPeopleFishing", renderingTweaks.remove("hideOtherPeopleFishing") ?: return)
}

internal fun `005_renameEtherwarpHelper`(json: JsonMap) {
	val uiAndVisuals = json["uiAndVisuals"] as? JsonMap ?: return
	uiAndVisuals.put("etherwarpOverlay", uiAndVisuals.remove("etherwarpHelper") ?: return)
}
