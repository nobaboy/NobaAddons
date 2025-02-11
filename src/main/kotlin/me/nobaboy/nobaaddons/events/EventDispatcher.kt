package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.utils.ErrorManager
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KFunction
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters

/**
 * Abstract event dispatcher implementation, providing a basic implementation of a Fabric-like event system
 * utilizing data classes as events
 *
 * @see EventDispatcher
 * @see ReturningEventDispatcher
 */
abstract class AbstractEventDispatcher<T : Event, R : Any?> protected constructor(
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

	private fun eventName(event: T): String {
		val parent = event::class.qualifiedName ?: "an event"
		return parent.split(".").asReversed().takeWhile { it.any(Char::isUpperCase) }.reversed().joinToString(".")
	}

	open fun registerFunction(function: KFunction<*>, instance: Any? = null) {
		require(function.valueParameters.size == 1) { "Provided function must accept exactly one parameter" }
		val eventParam = function.valueParameters.first()
		register { event ->
			function.callBy(buildMap {
				function.instanceParameter?.let { put(it, instance) }
				put(eventParam, event)
			})
		}
	}

	open fun register(listener: (T) -> Unit) {
		listeners.add(listener)
	}

	protected fun executeListeners(event: T) {
		listeners.forEach {
			try {
				it(event)
			} catch(e: Throwable) {
				if(!gracefulExceptions) throw e
				ErrorManager.logError("Encountered an exception while processing ${eventName(event)}", e)
			}
			if(event.canceled && exitEarlyOnCancel) return
		}
	}

	abstract fun invoke(event: T): R
}

/**
 * Basic [AbstractEventDispatcher] implementation which simply returns [Event.canceled]
 */
open class EventDispatcher<T : Event>(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
) : AbstractEventDispatcher<T, Boolean>(exitEarlyOnCancel, gracefulExceptions) {
	final override fun invoke(event: T): Boolean {
		executeListeners(event)
		return event.canceled
	}

	companion object {
		/**
		 * Convenience method to generate a [ReturningEventDispatcher] yielding the value of [Event.canceled]
		 */
		@Deprecated("This functionality has been merged into the base EventDispatcher")
		fun <T : Event> cancelable() = EventDispatcher<T>()
	}
}

/**
 * [AbstractEventDispatcher] implementation that returns the return value of [returns] when
 * an event is passed through it
 */
open class ReturningEventDispatcher<T : Event, R : Any?>(
	private val returns: (T) -> R,
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
) : AbstractEventDispatcher<T, R>(exitEarlyOnCancel, gracefulExceptions) {
	override fun invoke(event: T): R {
		executeListeners(event)
		return returns(event)
	}
}

@Deprecated("")
@Suppress("FunctionName")
fun <T : Event, R : Any?> EventDispatcher(
	exitEarlyOnCancel: Boolean = true,
	gracefulExceptions: Boolean = true,
	returns: (T) -> R
) = ReturningEventDispatcher(returns, exitEarlyOnCancel, gracefulExceptions)