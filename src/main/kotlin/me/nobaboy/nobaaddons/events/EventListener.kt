package me.nobaboy.nobaaddons.events

/**
 * Event listener invoked from an [EventDispatcher]
 */
fun interface EventListener<T : Event> {
	fun invoke(event: T)
}