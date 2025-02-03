package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.mixins.accessors.ChatHudAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.Text

/**
 * Captured chat message, providing access to the underlying [ChatHudLine]
 */
data class Message(val message: Text, val line: ChatHudLine, val visible: MutableList<ChatHudLine.Visible>) {
	/**
	 * Utility method to remove this message from the chat log
	 */
	fun remove() {
		val accessor = (MCUtils.client.inGameHud.chatHud as ChatHudAccessor)
		accessor.messages.remove(line)
		visible.forEach(accessor.visibleMessages::remove)
	}
}
