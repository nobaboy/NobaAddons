package me.nobaboy.nobaaddons.config.option

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

typealias Migration = Json.(MutableMap<String, JsonElement>) -> Unit

// TODO test this
class ConfigOptionMigration(private val migrations: List<Migration>) {
	val version = migrations.size

	fun apply(json: Json, obj: JsonObject): JsonObject {
		var configVersion = (obj["version"] as? JsonPrimitive)?.intOrNull ?: 0

		val toApply = migrations.slice(configVersion until version)
		if(toApply.isEmpty()) {
			return obj
		}

		return obj.modify {
			for(migration in toApply) {
				migration(json, it)
				it["version"] = JsonPrimitive(++configVersion)
			}
		}
	}
}

class MigrationBuilder {
	private val migrations = mutableListOf<Migration>()

	fun migration(migration: Migration) {
		migrations.add(migration)
	}

	fun build(): ConfigOptionMigration =
		ConfigOptionMigration(migrations.toList())
}

inline fun migrations(builder: MigrationBuilder.() -> Unit): ConfigOptionMigration =
	MigrationBuilder().apply(builder).build()

inline fun MutableMap<String, JsonElement>.modify(element: String, mod: (MutableMap<String, JsonElement>) -> Unit) {
	val elem = (this[element] as? JsonObject)?.toMutableMap() ?: return
	mod(elem)
	this[element] = JsonObject(elem)
}

inline fun JsonObject.modify(mod: (MutableMap<String, JsonElement>) -> Unit): JsonObject {
	return JsonObject(toMutableMap().apply(mod))
}

fun MutableMap<String, JsonElement>.deepModify(path: List<String>, modifier: (MutableMap<String, JsonElement>) -> Unit) {
	if(path.isEmpty()) {
		modifier(this)
		return
	}

	val path = path.toMutableList()
	val key = path.removeFirst()
	var item = this[key] as? JsonObject ?: JsonObject(mapOf())

	put(key, JsonObject(item.toMutableMap().apply { deepModify(path, modifier) }))
}