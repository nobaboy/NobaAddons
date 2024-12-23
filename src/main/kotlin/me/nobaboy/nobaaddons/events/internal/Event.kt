package me.nobaboy.nobaaddons.events.internal

abstract class Event(val isCancelable: Boolean = false) {
	@get:JvmName("isCanceled")
	var canceled: Boolean = false
		private set

	open fun cancel() {
		check(isCancelable) { "Attempted to cancel a non-cancelable event" }
		canceled = true
	}
}