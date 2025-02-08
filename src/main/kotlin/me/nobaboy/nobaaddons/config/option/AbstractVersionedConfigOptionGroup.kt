package me.nobaboy.nobaaddons.config.option

import dev.isxander.yacl3.api.ConfigCategory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull

/**
 * Abstract implementation of [ConfigOptionGroup], nesting the config within a `config` object
 * and allowing for applying migrations to loaded configurations.
 */
abstract class AbstractVersionedConfigOptionGroup(id: String) : AbstractConfigOptionGroup(id) {
	protected open val migrations: ConfigOptionMigration? = null

	var version: Int = migrations?.version ?: 0
		private set

	/**
	 * Load configs for all known [ConfigOption]s from the provided [JsonObject]
	 */
	override fun load(json: Json, obj: JsonObject) {
		migrations?.apply(json, obj)
		super.load(json, obj["config"] as? JsonObject ?: JsonObject(emptyMap()))
		version = (obj["version"] as? JsonPrimitive)?.intOrNull ?: migrations?.version ?: 0
	}

	/**
	 * Dump all current [ConfigOption]s to a [JsonObject]
	 */
	override fun dump(json: Json): JsonObject = buildJsonObject {
		put("config", super.dump(json))
		put("version", JsonPrimitive(version))
	}

	/**
	 * Implement your YACL config building here.
	 */
	abstract fun buildConfig(category: ConfigCategory.Builder)
}