package me.nobaboy.nobaaddons.events

/**
 * Convenience class that allows for an [Event] to be canceled
 */
abstract class CancelableEvent : Event {
	@get:JvmName("isCanceled")
	var canceled: Boolean = false
		private set

	open fun cancel() {
		canceled = true
	}
}