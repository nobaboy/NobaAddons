/**
 * This file is for utilities used in migrations; for the actual migrations themselves, see ../migrations.kt
 */

package me.nobaboy.nobaaddons.config.util

import dev.celestialfault.histoire.migrations.MutableJsonMap
import dev.celestialfault.histoire.migrations.takeAndRemove

object DiscardValue

/**
 * Move [key] from the current [MutableJsonMap] to the specified [target]
 */
fun MutableJsonMap.moveTo(key: String, target: MutableJsonMap, newKey: String = key) {
	takeAndRemove(key) { target[newKey] = it }
}

/**
 * Move [key] from the current [MutableJsonMap] to the specified [target]
 *
 * [mapping] may return [Unit] to retain the same value provided to the mapping function, or [DiscardValue] to discard it.
 */
inline fun MutableJsonMap.mapAndMoveTo(key: String, target: MutableJsonMap, newKey: String = key, mapping: (Any) -> Any) {
	takeAndRemove(key) {
		target[newKey] = when(val result = mapping(it)) {
			Unit -> it
			DiscardValue -> return
			else -> result
		}
	}
}

/**
 * Rename [old] to [new], while modifying the value stored in the process
 *
 * This is a convenience alias for `mapAndMoveTo(old, this, new) { ... }`
 */
inline fun MutableJsonMap.mapAndRename(old: String, new: String, mapping: (Any) -> Any) {
	mapAndMoveTo(old, this, new, mapping)
}
