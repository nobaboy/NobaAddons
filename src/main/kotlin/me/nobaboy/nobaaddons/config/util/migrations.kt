/**
 * This file is for utilities used in migrations; for the actual migrations themselves, see ../migrations.kt
 */

package me.nobaboy.nobaaddons.config.util

typealias JsonMap = MutableMap<String, Any>
typealias JsonList = MutableList<Any>

/**
 * Get or create a new [JsonMap]
 */
@Suppress("UNCHECKED_CAST")
fun JsonMap.getMap(vararg key: String): JsonMap {
	var parent = this
	for(k in key) {
		parent = parent.getOrPut(k) { mutableMapOf<String, Any>() } as JsonMap
	}
	return parent
}

/**
 * Get or create a new [JsonList]
 */
@Suppress("UNCHECKED_CAST")
fun JsonMap.getList(key: String): JsonList = getOrPut(key) { mutableListOf<Any>() } as JsonList

private fun mapValue(value: Any, mapping: (Any) -> Any?): Any? {
	val mapped = mapping(value)
	// TODO test if this even works how I hope it does
	return if(mapped == Unit) value else mapped
}

/**
 * Move [key] from the current [JsonMap] to the specified [target]
 */
fun JsonMap.moveTo(key: String, target: JsonMap, newKey: String = key) {
	target[newKey] = remove(key) ?: return
}

/**
 * Move [key] from the current [JsonMap] to the specified [target]
 *
 * [mapping] may either return `null` to remove the value, or [Unit] to retain the same value
 * (e.g. if the value was a [JsonMap] or [JsonList] that was modified in-place)
 */
fun JsonMap.mapAndMoveTo(key: String, target: JsonMap, newKey: String = key, mapping: (Any) -> Any?) {
	target[newKey] = mapValue(remove(key) ?: return, mapping) ?: return
}

/**
 * Rename [old] to [new]
 */
fun JsonMap.rename(old: String, new: String) {
	this[new] = remove(old) ?: return
}

/**
 * Rename [old] to [new], while modifying the value stored in the process
 *
 * [mapping] may either return `null` to remove the value, or [Unit] to retain the same value
 * (e.g. if the value was a [JsonMap] or [JsonList] that was modified in-place)
 */
fun JsonMap.mapAndRename(old: String, new: String, mapping: (Any) -> Any?) {
	this[new] = mapValue(remove(old) ?: return, mapping) ?: return
}
