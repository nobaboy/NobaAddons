package me.nobaboy.nobaaddons.utils.properties

/**
 * Holds a value of non-nullable type [T], providing utilities to get or set the value as needed.
 *
 * [set] operations are [synchronized], while [get] has no such guarantee.
 */
data class Holding<T : Any>(@Volatile private var value: T? = null) {
	private val lock = Any()

	/**
	 * Get the current held value, or `null` if no value is currently stored.
	 */
	fun get(): T? = value

	/**
	 * Set the current stored value
	 */
	fun set(value: T) = synchronized(lock) {
		this.value = value
	}

	/**
	 * Clear the currently held value
	 */
	fun clear() = synchronized(lock) {
		value = null
	}

	/**
	 * Clears the currently held value, and then runs the provided [cleanup] callable
	 *
	 * If there is no value stored, this method effectively acts as a noop.
	 */
	fun clearWithCleanup(cleanup: (T) -> Unit) = synchronized(lock) {
		val removed = value ?: return
		value = null
		cleanup(removed)
	}

	/**
	 * Get the currently held value, or create one with the provided [factory] if no value is currently stored.
	 */
	fun getOrSet(factory: () -> T): T = synchronized(lock) {
		value ?: factory().also { value = it }
	}
}