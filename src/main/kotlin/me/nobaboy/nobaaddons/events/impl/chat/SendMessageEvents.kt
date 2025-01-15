package me.nobaboy.nobaaddons.events.impl.chat

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher

object SendMessageEvents {
	// TODO is there any use in adding a chat message event?
	// TODO could this be changed to be a wrapper around the fabric send message events?

	/**
	 * Event invoked before a command is sent by the player to the server
	 */
	@JvmField val SEND_COMMAND = EventDispatcher.Companion.cancelable<SendCommand>()

	data class SendCommand(val command: String) : Event(isCancelable = true)
}