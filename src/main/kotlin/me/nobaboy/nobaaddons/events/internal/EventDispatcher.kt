package me.nobaboy.nobaaddons.events.internal

open class EventDispatcher<T : Event>  {
	protected val listeners: MutableList<(T) -> Unit> = mutableListOf()

	open fun register(listener: (T) -> Unit) {
		listeners.add(listener)
	}

	open fun invoke(event: T) {
		listeners.forEach {
			it(event)
			if(event.canceled && event.exitEarlyOnCancel) return
		}
	}
}