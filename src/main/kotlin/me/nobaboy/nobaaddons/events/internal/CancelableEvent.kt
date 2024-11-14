package me.nobaboy.nobaaddons.events.internal

abstract class CancelableEvent {
	var canceled: Boolean = false
		protected set

	fun cancel() {
		this.canceled = true
	}
}