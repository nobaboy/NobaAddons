package me.nobaboy.nobaaddons.events

data class Listener<T : Event>(val dispatcher: AbstractEventDispatcher<T, *>, val listener: (T) -> Unit) {
	fun unsubscribe(): Boolean = dispatcher.listeners.remove(this)
}