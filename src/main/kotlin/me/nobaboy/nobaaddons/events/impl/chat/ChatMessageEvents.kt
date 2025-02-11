package me.nobaboy.nobaaddons.events.impl.chat

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.events.ReturningEventDispatcher
import me.nobaboy.nobaaddons.utils.chat.Message
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

/**
 * Chat message events; these events are invoked in the following order:
 *
 * - [CHAT]
 * - [ALLOW]
 * - [MODIFY]
 * - [ADDED]
 */
object ChatMessageEvents {
	init {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay -> overlay || !ALLOW.invoke(Allow(message)) }
		ClientReceiveMessageEvents.MODIFY_GAME.register { message, overlay -> if(overlay) message else MODIFY.invoke(Modify(message)) }
	}

	@JvmField val CHAT = Chat.Companion
	val ALLOW = Allow.Companion
	val MODIFY = Modify.Companion
	@JvmField val ADDED = Added.Companion

	/**
	 * Event invoked upon receiving a chat message from the server.
	 *
	 * This event is invoked **before** Fabric API events, and as such runs before [Allow] and [Modify],
	 * and is not dependent on mod load order.
	 */
	data class Chat(val message: Text) : Event() {
		companion object : EventDispatcher<Chat>()
	}

	/**
	 * Event invoked to determine if a message should be allowed to be sent in chat.
	 *
	 * This event is a wrapper around the Fabric API event, and as such is dependent on mod load order,
	 * and will not be invoked if a previous listener already canceled the message.
	 *
	 * Note that this event is invoked **after** [Chat].
	 */
	data class Allow(val message: Text) : Event(true) {
		companion object : EventDispatcher<Allow>()
	}

	/**
	 * Allows for modifying received chat messages
	 *
	 * This event is a wrapper around the Fabric API event, and as such is dependent on mod load order,
	 * and will reflect any modifications applied by previous listeners.
	 */
	data class Modify(var message: Text) : Event() {
		companion object : ReturningEventDispatcher<Modify, Text>({ it.message })
	}

	/**
	 * Event invoked after a chat message has been added to the chat HUD.
	 *
	 * This event provides access to the underlying [net.minecraft.client.gui.hud.ChatHudLine], and the visible
	 * counterparts, along with an easy way to remove them afterward ([Message.remove]).
	 */
	data class Added(val message: Message) : Event() {
		companion object : EventDispatcher<Added>()
	}
}