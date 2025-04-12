package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.utils.ErrorManager
import java.util.concurrent.CopyOnWriteArrayList

// -Dnobaaddons.events.print=RenderEvents.Render;PacketEvents.Send
private val LOG_EVENTS = System.getProperty("nobaaddons.events.print").split(";")
// -Dnobaaddons.events.print.all=true
private val LOG_ALL_EVENTS = System.getProperty("nobaaddons.events.print.all") == "true"
// -Dnobaaddons.events.print.canceled=true
private val LOG_CANCELED = System.getProperty("nobaaddons.events.print.canceled") == "true"

private fun eventName(event: Any): String? {
	val parent = event::class.qualifiedName ?: return null
	return parent.split(".").asReversed().takeWhile { it.any(Char::isUpperCase) }.reversed().joinToString(".")
}

private fun shouldLog(event: Any): Boolean {
	if(LOG_ALL_EVENTS) return true
	if(LOG_EVENTS.isEmpty()) return false
	return eventName(event) in LOG_EVENTS
}

/**
 * Abstract event dispatcher implementation, providing a basic implementation of a Fabric-like event system
 * utilizing data classes as events
 */
abstract class EventDispatcher<T : Event, R : Any?> protected constructor(
	/**
	 * If `true`, the event dispatcher will stop invoking listeners upon the first one canceling the event.
	 */
	protected val exitEarlyOnCancel: Boolean = true,
	/**
	 * If `true`, the event dispatcher will send a message in chat when an error is encountered.
	 * If `false`, the error will be allowed to propogate to the [invoke] caller (which may crash the game).
	 *
	 * This should be left as its default of `true` unless you know that this event cannot gracefully fail in any capacity.
	 */
	protected val gracefulExceptions: Boolean = true,
) {
	private val listeners = CopyOnWriteArrayList<(T) -> Unit>()

	open fun register(listener: (T) -> Unit) {
		listeners.add(listener)
	}

	protected fun executeListeners(event: T) {
		if(shouldLog(event)) println("Event invoked: $event")
		listeners.forEach {
			try {
				it(event)
			} catch(e: Throwable) {
				if(!gracefulExceptions) throw e
				ErrorManager.logError("Encountered an exception while processing ${eventName(event)}", e)
			}
			if(event is CancelableEvent && event.canceled && exitEarlyOnCancel) {
				if(LOG_CANCELED || shouldLog(event)) println("Canceled $event")
				return
			}
		}
	}

	abstract fun invoke(event: T): R

	companion object {
		/**
		 * Convenience method to generate an [EventDispatcher] yielding the value of [CancelableEvent.canceled]
		 */
		fun <T : CancelableEvent> cancelable() = EventDispatcher<T, Boolean>(returns = CancelableEvent::canceled)
	}
}

/**
 * Create an [EventDispatcher] that returns nothing
 */
fun <T : Event> EventDispatcher(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
): EventDispatcher<T, Unit> = object : EventDispatcher<T, Unit>(exitEarlyOnCancel, gracefulExceptions) {
	override fun invoke(event: T) {
		executeListeners(event)
	}
}

/**
 * Create an [EventDispatcher] returning the value of [returns]
 */
inline fun <T : Event, R : Any?> EventDispatcher(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
	crossinline returns: (T) -> R
): EventDispatcher<T, R> = object : EventDispatcher<T, R>(exitEarlyOnCancel, gracefulExceptions) {
	override fun invoke(event: T): R {
		executeListeners(event)
		return returns(event)
	}
}
