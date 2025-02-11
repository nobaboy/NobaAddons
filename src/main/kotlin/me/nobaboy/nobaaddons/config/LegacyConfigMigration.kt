package me.nobaboy.nobaaddons.config

import me.nobaboy.nobaaddons.config.option.deepModify
import me.nobaboy.nobaaddons.config.option.modify
import kotlinx.serialization.json.JsonObject as KJsonObject
import com.google.gson.JsonObject as GsonJsonObject
import kotlinx.serialization.json.JsonPrimitive as KJsonPrimitive
import kotlinx.serialization.json.JsonElement as KJsonElement
import com.google.gson.JsonElement as GsonJsonElement

class LegacyConfigMigration private constructor(
	oldKey: String,
	newKey: String,
	val mapper: (GsonJsonElement) -> KJsonElement,
) {
	val oldPath = oldKey.split('.')
	val newPath = newKey.split('.')

	fun apply(old: GsonJsonObject, new: KJsonObject): KJsonObject {
		var oldJson: GsonJsonElement = old
		oldPath.iterator().let {
			for(path in it) {
				oldJson as GsonJsonObject
				if(!oldJson.has(path)) return new
				oldJson = oldJson[path]
				if(it.hasNext() && oldJson !is GsonJsonObject) return new
			}
		}

		// if only JsonObject was mutable, maybe this would be a lot less of a headache to make
		return new.modify {
			it.deepModify(newPath.toMutableList().also { it.removeLast() }) {
				it.put(newPath.last(), mapper(oldJson))
			}
		}
	}

	companion object {
		// a few notes on some options that were skipped:
		//  - chat alerts are skipped entirely, as the keys for them were incorrectly shared between both settings
		val FEATURES = buildList<LegacyConfigMigration> {
			boolean("inventory.itemPickupLog.enabled", "itemPickupLog.config.enabled")
			int("inventory.itemPickupLog.timeoutSeconds", "itemPickupLog.config.timeoutSeconds")

			boolean("slayers.compactMessages.enabled", "compactSlayerMessages.config.enabled")

			boolean("slayers.highlightMiniBosses.enabled", "slayerMiniBosses.config.highlight")
			int("slayers.highlightMiniBosses.highlightColor", "slayerMiniBosses.config.highlightColor")
			boolean("slayers.miniBossAlert.enabled", "slayerMiniBosses.config.spawnAlert")
			int("slayers.miniBossAlert.alertColor", "slayerMiniBosses.config.alertColor")

			boolean("qol.garden.reduceMouseSensitivity", "mouseLock.config.reduceMouseSensitivity")
			boolean("qol.garden.autoUnlockMouseOnTeleport", "mouseLock.config.autoUnlockMouseOnTeleport")
			int("qol.garden.reductionMultiplier", "mouseLock.config.reductionMultiplier")
		}

		val CORE = buildList<LegacyConfigMigration> {
			string("repo.branch", "api.config.branch")
			string("repo.username", "api.config.username")
			string("repo.repository", "api.config.repository")
			boolean("repo.autoUpdate", "api.config.autoUpdate")
		}

		private fun MutableList<LegacyConfigMigration>.boolean(old: String, new: String) {
			add(LegacyConfigMigration(old, new) { KJsonPrimitive(it.asBoolean) })
		}

		private fun MutableList<LegacyConfigMigration>.int(old: String, new: String) {
			add(LegacyConfigMigration(old, new) { KJsonPrimitive(it.asInt) })
		}

		private fun MutableList<LegacyConfigMigration>.string(old: String, new: String) {
			add(LegacyConfigMigration(old, new) { KJsonPrimitive(it.asString) })
		}

		fun List<LegacyConfigMigration>.applyAll(old: GsonJsonObject, new: KJsonObject): KJsonObject {
			var new = new
			for(migration in this) {
				new = migration.apply(old, new)
			}
			return new
		}
	}
}