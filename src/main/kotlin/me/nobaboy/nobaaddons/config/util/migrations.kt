/**
 * This file is for utilities used in migrations; for the actual migrations themselves, see ../migrations.kt
 */

package me.nobaboy.nobaaddons.config.util

import dev.celestialfault.histoire.migrations.MutableJsonList
import dev.celestialfault.histoire.migrations.MutableJsonMap
import dev.celestialfault.histoire.migrations.takeAndRemove

/**
 * Move [key] from the current [MutableJsonMap] to the specified [target]
 */
fun MutableJsonMap.moveTo(key: String, target: MutableJsonMap, newKey: String = key) {
	takeAndRemove(key) { target[newKey] = it }
}

/**
 * Move [key] from the current [MutableJsonMap] to the specified [target]
 *
 * [mapping] may either return `null` to remove the value, or [Unit] to retain the same value
 * (e.g. if the value was a [MutableJsonMap] or [MutableJsonList] that was modified in-place)
 */
inline fun MutableJsonMap.mapAndMoveTo(key: String, target: MutableJsonMap, newKey: String = key, mapping: (Any) -> Any?) {
	takeAndRemove(key) {
		target[newKey] = when(val result = mapping(it)) {
			null -> return
			Unit -> it
			else -> result
		}
	}
}

/**
 * Rename [old] to [new], while modifying the value stored in the process
 *
 * [mapping] may either return `null` to remove the value, or [Unit] to retain the same value
 * (e.g. if the value was a [MutableJsonMap] or [MutableJsonList] that was modified in-place)
 */
inline fun MutableJsonMap.mapAndRename(old: String, new: String, mapping: (Any) -> Any?) {
	takeAndRemove(old) {
		this[new] = when(val result = mapping(it)) {
			null -> return
			Unit -> it
			else -> result
		}
	}
}
