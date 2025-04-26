package me.nobaboy.nobaaddons.features

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.events.impl.client.TickEvents

class FeatureDeclaration internal constructor(val feature: AbstractFeature) {
	/**
	 * Declare a listener for a given event [dispatcher]
	 */
	inline fun <T : Event> listen(dispatcher: EventDispatcher<T, *>, crossinline listener: (T) -> Unit) {
		dispatcher.register {
			if(feature.killSwitch) return@register
			listener(it)
		}
	}

	/**
	 * Convenience method to declare a [TickEvents.TICK] listener, optionally only being invoked [every Nth tick][every].
	 *
	 * If [every] is `null`, this functions the same as `listen(TickEvents.TICK, listener)`.
	 */
	inline fun tick(every: Int? = null, crossinline listener: (TickEvents.Tick) -> Unit) {
		if(every == null) {
			return listen(TickEvents.TICK, listener)
		}

		TickEvents.every(every) {
			if(feature.killSwitch) return@every
			listener(it)
		}
	}
}