package me.nobaboy.nobaaddons.events.impl.chat

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

/**
 * Chat message events; these events are invoked in the following order:
 *
 * - [CHAT]
 * - [ALLOW]
 * - [MODIFY]
 * - [LATE_MODIFY]
 */
object ChatMessageEvents {
	init {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay -> overlay || !ALLOW.invoke(Allow(message)) }
		ClientReceiveMessageEvents.MODIFY_GAME.register { message, overlay -> if(overlay) message else MODIFY.invoke(Modify(message)) }
	}

	/**
	 * Event invoked upon receiving a chat message from the server.
	 *
	 * This event is invoked **before** Fabric API events, and as such runs before [ALLOW] and [MODIFY],
	 * and is not dependent on mod load order.
	 */
	@JvmField val CHAT = EventDispatcher<Chat>()

	/**
	 * Event invoked to determine if a message should be allowed to be sent in chat.
	 *
	 * This event is a wrapper around the Fabric API event, and as such is dependent on mod load order,
	 * and will not be invoked if a previous listener already canceled the message.
	 *
	 * Note that this event is invoked **after** [CHAT].
	 */
	val ALLOW = EventDispatcher.cancelable<Allow>()

	/**
	 * Allows for modifying received chat messages
	 *
	 * This event is a wrapper around the Fabric API event, and as such is dependent on mod load order,
	 * and will reflect any modifications applied by previous listeners.
	 */
	val MODIFY = EventDispatcher<Modify, Text> { it.message }

	/**
	 * Event invoked as late as possible in the chat message receive process, after almost all other mods have
	 * already applied any applicable modifications (including compact chat mods).
	 *
	 * This is intended to allow for modifying a message while using other compact chat mods the
	 * client may have installed.
	 */
	@JvmField val LATE_MODIFY = EventDispatcher<Modify, Text> { it.message }

	data class Chat(val message: Text) : Event()
	data class Allow(val message: Text) : Event(true)
	data class Modify(var message: Text) : Event()
}