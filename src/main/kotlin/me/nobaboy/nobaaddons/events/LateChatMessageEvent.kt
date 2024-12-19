package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.text.Text

data class LateChatMessageEvent(var message: Text) : Event() {
	companion object {
		/**
		 * Event invoked right before a chat message is added to the chat history, allowing for applying modifications
		 * after most other mods (like compact chat mods).
		 */
		@JvmField val EVENT = EventDispatcher<LateChatMessageEvent>()
	}
}
