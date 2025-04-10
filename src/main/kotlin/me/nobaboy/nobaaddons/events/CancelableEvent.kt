package me.nobaboy.nobaaddons.events

/**
 * Generic cancelable [Event] implementation
 */
abstract class CancelableEvent : Event {
	@get:JvmName("isCanceled")
	var canceled: Boolean = false
		private set

	open fun cancel() {
		canceled = true
	}
}