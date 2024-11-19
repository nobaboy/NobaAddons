package me.nobaboy.nobaaddons.events.internal

abstract class CancelableEvent {
	@get:JvmName("isCanceled")
	var canceled: Boolean = false
		protected set

	fun cancel() {
		this.canceled = true
	}
}