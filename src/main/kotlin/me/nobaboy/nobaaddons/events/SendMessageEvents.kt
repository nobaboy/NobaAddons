package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher

object SendMessageEvents {
	// TODO is there any use in adding a chat message event?

	/**
	 * Event invoked before a command is sent by the player to the server
	 */
	@JvmField val SEND_COMMAND = EventDispatcher.cancelable<SendCommand>()

	data class SendCommand(val command: String) : Event(isCancelable = true)
}
