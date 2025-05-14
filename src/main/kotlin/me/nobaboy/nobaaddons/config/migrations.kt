// This file intentionally violates this style rule to make the migration application order unquestionably clear.
@file:Suppress("FunctionName", "UNCHECKED_CAST")

package me.nobaboy.nobaaddons.config

import dev.celestialfault.histoire.migrations.Migrations
import dev.celestialfault.histoire.migrations.MutableJsonMap
import dev.celestialfault.histoire.migrations.getMap
import dev.celestialfault.histoire.migrations.rename
import kotlinx.serialization.json.JsonPrimitive
import me.nobaboy.nobaaddons.config.util.mapAndMoveTo
import me.nobaboy.nobaaddons.config.util.modify
import me.nobaboy.nobaaddons.config.util.moveTo

/*
 * Migrations MUST be added at the end of this block, otherwise they won't run correctly. Migrations that have
 * already been applied and saved are not run again, so new changes must be added as separate migrations. Removing
 * migrations is NOT supported and will cause player configs to break, so avoid doing so.
 */
internal val migrations = Migrations("configVersion") {
	add(::`001_removeYaclVersion`)
	add(::`002_inventoryCategory`)
	add(::`003_renameGlaciteMineshaftShareCorpses`)
	add(::`004_moveHideOtherPeopleFishing`)
	add(::`005_renameEtherwarpHelper`)
	add(::`006_renameSeaCreatureChatFilter`)
	add(::`007_renameCopyChat`)
	add(::`008_replaceAwtColorWithNobaColor`)
}

private fun `001_removeYaclVersion`(json: MutableJsonMap) {
	json.remove("version")
}

private fun `002_inventoryCategory`(json: MutableJsonMap) {
	val uiAndVisuals = json.getMap("uiAndVisuals")
	val inventory = json.getMap("inventory")

	uiAndVisuals.mapAndMoveTo("slotInfo", inventory) {
		(it as MutableJsonMap).remove("enabled")
		Unit
	}

	uiAndVisuals.mapAndMoveTo("enchantments", inventory, newKey = "enchantmentTooltips") {
		(it as MutableJsonMap).rename("parseItemEnchants", "modifyTooltips")
	}
}

private fun `003_renameGlaciteMineshaftShareCorpses`(json: MutableJsonMap) {
	val glaciteMineshaft = json.getMap("mining", "glaciteMineshaft")
	glaciteMineshaft.rename("autoShareCorpseCoords", "autoShareCorpses")
}

private fun `004_moveHideOtherPeopleFishing`(json: MutableJsonMap) {
	val renderingTweaks = json.getMap("uiAndVisuals", "renderingTweaks")
	val fishing = json.getMap("fishing")
	renderingTweaks.moveTo("hideOtherPeopleFishing", fishing)
}

private fun `005_renameEtherwarpHelper`(json: MutableJsonMap) {
	val uiAndVisuals = json.getMap("uiAndVisuals")
	uiAndVisuals.rename("etherwarpHelper", "etherwarpOverlay")
}

private fun `006_renameSeaCreatureChatFilter`(json: MutableJsonMap) {
	val filters = json.getMap("chat", "filters")
	filters.rename("hideSeaCreatureSpawnMessage", "hideSeaCreatureCatchMessage")
	filters.rename("seaCreatureMaximumRarity", "seaCreatureMaxRarity")
}

private fun `007_renameCopyChat`(json: MutableJsonMap) {
	val chat = json.getMap("chat")
	chat.rename("copy", "copyChat")
}

private fun `008_replaceAwtColorWithNobaColor`(json: MutableJsonMap) {
	val voidgloom = json.getMap("slayers", "voidgloom")

	listOf(
		"beaconPhaseColor",
		"hitsPhaseColor",
		"damagePhaseColor",
	).forEach { key ->
		voidgloom.modify(key) { value ->
			if(value is JsonPrimitive) {
				val (r, g, b, _) = value.content.split(":").map { it.toInt() }
				JsonPrimitive((r shl 16) or (g shl 8) or b)
			} else value
		}
	}
}
