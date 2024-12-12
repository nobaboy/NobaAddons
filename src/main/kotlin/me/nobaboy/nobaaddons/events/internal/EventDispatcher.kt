package me.nobaboy.nobaaddons.events.internal

// TODO change this to require T to extend Event
open class EventDispatcher<T> : AbstractEventDispatcher<T>() {
	protected val listeners: MutableList<(T) -> Unit> = mutableListOf()

	override fun register(listener: (T) -> Unit) {
		listeners.add(listener)
	}

	override fun invoke(event: T) {
		// TODO merge CancelableEventDispatcher into this (with a separate val on Event to enable the early exit?)
		listeners.forEach { it(event) }
	}
}