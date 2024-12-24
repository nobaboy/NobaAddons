package me.nobaboy.nobaaddons.events.internal

/**
 * Abstract event dispatcher implementation, providing a basic implementation of a Fabric-like event system
 * utilizing data classes as events
 *
 * @see EventDispatcher
 * @see ReturningEventDispatcher
 */
abstract class AbstractEventDispatcher<T : Event, R : Any?>(protected val exitEarlyOnCancel: Boolean = true) {
	protected val listeners: MutableList<(T) -> Unit> = mutableListOf()

	open fun register(listener: (T) -> Unit) {
		listeners.add(listener)
	}

	protected open fun executeListeners(event: T) {
		listeners.forEach {
			it(event)
			if(event.canceled && exitEarlyOnCancel) return
		}
	}

	abstract fun invoke(event: T): R
}

/**
 * Basic [AbstractEventDispatcher] implementation that doesn't return anything.
 */
open class EventDispatcher<T : Event>(exitEarlyOnCancel: Boolean = true) : AbstractEventDispatcher<T, Unit>(exitEarlyOnCancel) {
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
	private val returns: (T) -> R, exitEarlyOnCancel: Boolean = true
) : AbstractEventDispatcher<T, R>(exitEarlyOnCancel) {
	override fun invoke(event: T): R {
		executeListeners(event)
		return returns(event)
	}
}

@Suppress("FunctionName")
fun <T : Event, R : Any?> EventDispatcher(exitEarlyOnCancel: Boolean = true, returns: (T) -> R) = ReturningEventDispatcher(returns, exitEarlyOnCancel)
