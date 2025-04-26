package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.utils.ErrorManager
import java.util.concurrent.CopyOnWriteArrayList

private fun eventName(event: Any): String {
	val parent = event::class.qualifiedName ?: "an event"
	return parent.split(".").asReversed().takeWhile { it.any(Char::isUpperCase) }.reversed().joinToString(".")
}

/**
 * Generic event dispatcher providing a basic implementation of a Fabric-like event system
 * utilizing `(data )class`es as events instead of `fun interface`s
 */
open class EventDispatcher<T : Event, R : Any?>(
	/**
	 * If `true`, the event dispatcher will stop invoking further listeners upon the first one that cancels the
	 * event if [T] is a [CancelableEvent].
	 */
	protected val exitEarlyOnCancel: Boolean = true,
	/**
	 * If `true`, the event dispatcher will send a message in chat when an error is encountered and continue invoking listeners.
	 * If `false`, the error will simply be re-thrown out of [dispatch], which may result in the game crashing if not caught.
	 *
	 * This should be left as its default of `true` unless you know that this event cannot gracefully fail in any capacity.
	 */
	protected val gracefulExceptions: Boolean = true,
	/**
	 * Returns the value provided by [dispatch]
	 */
	protected val returns: (T) -> R,
) {
	private val listeners = CopyOnWriteArrayList<EventListener<T>>()

	/**
	 * Register a new [EventListener]
	 */
	fun register(listener: EventListener<T>) {
		listeners.add(listener)
	}

	/**
	 * Dispatch an event through this event's listeners
	 */
	fun dispatch(event: T): R {
		for(listener in listeners) {
			try {
				listener.invoke(event)
			} catch(e: Throwable) {
				if(!gracefulExceptions) {
					throw e
				}
				ErrorManager.logError("Encountered an exception while processing ${eventName(event)}", e)
			}
			if(event is CancelableEvent && event.canceled && exitEarlyOnCancel) {
				break
			}
		}
		return returns(event)
	}

	companion object {
		/**
		 * Create a new [EventDispatcher] that returns the value of [CancelableEvent.canceled]
		 */
		fun <T : CancelableEvent> cancelable() = EventDispatcher<T, Boolean>(returns = CancelableEvent::canceled)

		/**
		 * Register a listener that only invokes [listener] if the invoked event type is of type [T].
		 *
		 * This is primarily useful for event dispatchers that run multiple subtypes through the
		 * same dispatcher, like [BlockInteractionEvent][me.nobaboy.nobaaddons.events.impl.interact.BlockInteractionEvent].
		 */
		inline fun <reified T : Event> EventDispatcher<in T, *>.registerIf(crossinline listener: (T) -> Unit) {
			register { if(it is T) listener(it) }
		}
	}
}

/**
 * Convenience overload; creates a new event dispatcher that doesn't have a return value.
 */
fun <T : Event> EventDispatcher(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
): EventDispatcher<T, Unit> = EventDispatcher(
	exitEarlyOnCancel = exitEarlyOnCancel,
	gracefulExceptions = gracefulExceptions,
	returns = {},
)
