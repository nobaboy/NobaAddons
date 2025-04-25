package me.nobaboy.nobaaddons.events.impl.chat

import me.nobaboy.nobaaddons.events.CancelableEvent
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents

object SendMessageEvents {
	init {
		ClientSendMessageEvents.CHAT.register { SEND_CHAT_MESSAGE.invoke(SendMessage(it)) }
	}

	// TODO could this be changed to be a wrapper around the fabric send message events?

	/**
	 * Event invoked when the player sends a chat message
	 *
	 * This event is a wrapper around [ClientSendMessageEvents.CHAT]
	 */
	val SEND_CHAT_MESSAGE = EventDispatcher<SendMessage>()

	/**
	 * Event invoked before a command is sent by the player to the server
	 */
	@JvmField val SEND_COMMAND = EventDispatcher.cancelable<SendCommand>()

	data class SendMessage(val message: String) : Event
	data class SendCommand(val command: String) : CancelableEvent()
}