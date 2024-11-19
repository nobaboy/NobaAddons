package me.nobaboy.nobaaddons.events.internal

/**
 * An alternative form of [EventDispatcher] that exits early when a listener cancels the current event
 */
class CancelableEventDispatcher<T : CancelableEvent> : EventDispatcher<T>() {
	override fun invoke(event: T) {
		listeners.forEach {
			it(event)
			if(event.canceled) {
				return
			}
		}
	}
}