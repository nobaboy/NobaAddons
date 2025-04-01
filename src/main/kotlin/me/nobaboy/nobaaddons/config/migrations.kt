// This file intentionally violates this style rule to make the migration application order unquestionably clear.
@file:Suppress("FunctionName", "UNCHECKED_CAST")

package me.nobaboy.nobaaddons.config

import dev.celestialfault.histoire.migrations.Migrations
import kotlinx.serialization.json.JsonPrimitive

private typealias JsonMap = MutableMap<String, Any>

/*
 * Migrations MUST be added at the end of this block, otherwise they will NOT run. Migrations that have already been
 * applied are skipped, so new changes must be added as separate migrations. Removing pre-existing migrations is
 * NOT supported and will cause player configs to completely break, so avoid doing so.
 */
internal val migrations = Migrations("configVersion") {
	add(::`001_removeYaclVersion`)
	add(::`002_inventoryCategory`)
	add(::`003_renameGlaciteMineshaftShareCorpses`)
	add(::`004_moveHideOtherPeopleFishing`)
	add(::`005_renameEtherwarpHelper`)
}

private fun `001_removeYaclVersion`(json: JsonMap) {
	json.remove("version")
}

private fun `002_inventoryCategory`(json: JsonMap) {
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

private fun `003_renameGlaciteMineshaftShareCorpses`(json: JsonMap) {
	val glaciteMineshaft = json["mining"]?.let { it as? JsonMap }?.get("glaciteMineshaft") as? JsonMap ?: return
	glaciteMineshaft.put("autoShareCorpses", glaciteMineshaft.remove("autoShareCorpseCoords") ?: return)
}

private fun `004_moveHideOtherPeopleFishing`(json: JsonMap) {
	val renderingTweaks = json["uiAndVisuals"]?.let { it as? JsonMap }?.get("renderingTweaks") as? JsonMap ?: return
	val fishing = json.getOrPut("fishing") { mutableMapOf<String, Any>() } as JsonMap
	fishing.put("hideOtherPeopleFishing", renderingTweaks.remove("hideOtherPeopleFishing") ?: return)
}

private fun `005_renameEtherwarpHelper`(json: JsonMap) {
	val uiAndVisuals = json["uiAndVisuals"] as? JsonMap ?: return
	uiAndVisuals.put("etherwarpOverlay", uiAndVisuals.remove("etherwarpHelper") ?: return)
}
