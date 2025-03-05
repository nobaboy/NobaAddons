package me.nobaboy.nobaaddons.events

interface Event {
	val canceled: Boolean
}

abstract class AbstractEvent(val isCancelable: Boolean = false) : Event {
	final override var canceled: Boolean = false
		private set

	open fun cancel() {
		check(isCancelable) { "Attempted to cancel a non-cancelable event" }
		canceled = true
	}
}