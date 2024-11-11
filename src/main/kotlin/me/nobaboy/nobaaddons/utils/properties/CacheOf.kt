package me.nobaboy.nobaaddons.utils.properties

import kotlin.reflect.KProperty

class CacheOf<R : Any?>(private val cached: () -> Any, private val getter: () -> R) {
	private val lock = Any()

	@Volatile private var retValue: Any? = MISSING
	@Volatile private var lastCache: Any? = MISSING

	operator fun getValue(instance: Any, property: KProperty<*>): R {
		synchronized(lock) {
			val currentValue = this.cached()
			if(retValue === MISSING || lastCache != currentValue) {
				this.retValue = getter()
				this.lastCache = currentValue
			}
		}

		@Suppress("UNCHECKED_CAST")
		return retValue as R
	}
}

private object MISSING