package me.nobaboy.nobaaddons.events.internal

open class EventDispatcher<T> : AbstractEventDispatcher<T>() {
	protected val listeners: MutableList<(T) -> Unit> = mutableListOf()

	override fun register(listener: (T) -> Unit) {
		listeners.add(listener)
	}

	override fun invoke(event: T) {
		listeners.forEach { it(event) }
	}
}