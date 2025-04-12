package me.nobaboy.nobaaddons.repo

import kotlin.reflect.KProperty

/**
 * Supplies the provided handle [entries] in a list after unwrapping their contained values
 */
class RepoHandleList<T>(val entries: Collection<RepoHandle<T>>) {
	val value: List<T> get() = entries.map(RepoHandle<T>::value)

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): List<T> = value
}