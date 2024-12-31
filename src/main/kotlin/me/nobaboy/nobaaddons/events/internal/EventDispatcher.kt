package me.nobaboy.nobaaddons.events.internal

import me.nobaboy.nobaaddons.utils.ErrorManager

/**
 * Abstract event dispatcher implementation, providing a basic implementation of a Fabric-like event system
 * utilizing data classes as events
 *
 * @see EventDispatcher
 * @see ReturningEventDispatcher
 */
abstract class AbstractEventDispatcher<T : Event, R : Any?>(
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
	private val lock = Any()
	private var listeners: Array<(T) -> Unit> = emptyArray()

	open fun register(listener: (T) -> Unit) {
		synchronized(lock) {
			listeners = listeners.plus(listener)
		}
	}

	protected fun executeListeners(event: T) {
		listeners.forEach {
			try {
				it(event)
			} catch(e: Throwable) {
				if(!gracefulExceptions) throw e
				ErrorManager.logError("Encountered an exception while processing ${event::class.simpleName}", e)
			}
			if(event.canceled && exitEarlyOnCancel) return
		}
	}

	abstract fun invoke(event: T): R
}

/**
 * Basic [AbstractEventDispatcher] implementation that doesn't return anything.
 */
open class EventDispatcher<T : Event>(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
) : AbstractEventDispatcher<T, Unit>(exitEarlyOnCancel, gracefulExceptions) {
	override fun invoke(event: T) = executeListeners(event)

	companion object {
		/**
		 * Convenience method to generate a [ReturningEventDispatcher] yielding the value of [Event.canceled]
		 */
		fun <T : Event> cancelable() = EventDispatcher<T, Boolean> { it.canceled }
	}
}

/**
 * [AbstractEventDispatcher] implementation that returns the return value of [returns] when
 * an event is passed through it
 */
class ReturningEventDispatcher<T : Event, R : Any?>(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
	private val returns: (T) -> R
) : AbstractEventDispatcher<T, R>(exitEarlyOnCancel, gracefulExceptions) {
	override fun invoke(event: T): R {
		executeListeners(event)
		return returns(event)
	}
}

@Suppress("FunctionName")
fun <T : Event, R : Any?> EventDispatcher(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
	returns: (T) -> R
) = ReturningEventDispatcher(exitEarlyOnCancel, gracefulExceptions, returns)
