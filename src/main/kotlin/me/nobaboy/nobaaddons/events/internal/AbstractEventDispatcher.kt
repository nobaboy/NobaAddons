package me.nobaboy.nobaaddons.events.internal

abstract class AbstractEventDispatcher<T> {
	abstract fun register(listener: (T) -> Unit)
	abstract fun invoke(event: T)
}