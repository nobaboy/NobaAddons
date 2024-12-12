package me.nobaboy.nobaaddons.events.internal

abstract class Event(val isCancelable: Boolean = false) {
	@get:JvmName("isCanceled")
	var canceled: Boolean = false
		private set

	open fun cancel() {
		require(isCancelable) { "This event cannot be canceled" }
		canceled = true
	}
}