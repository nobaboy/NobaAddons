package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.utils.ErrorManager

/**
 * Utility class that invokes all provided [toInvoke] listeners; this is designed for features like
 * [IChatFilter][me.nobaboy.nobaaddons.features.chat.filters.IChatFilter].
 */
abstract class MultiEventInvoker<E : Event, T : Any>(
	dispatcher: AbstractEventDispatcher<E, *>,
	vararg toInvoke: T,
	private val invoker: T.(E) -> Unit,
) {
	private val toInvoke = toInvoke.toList()

	init {
		dispatcher.register(this::invoke)
	}

	private fun invoke(event: E) {
		for(listener in toInvoke) {
			try {
				invoker(listener, event)
			} catch(ex: Throwable) {
				ErrorManager.logError("Encountered an error while processing ${eventName(event)}", ex)
			}
			if(event.canceled) {
				return
			}
		}
	}
}