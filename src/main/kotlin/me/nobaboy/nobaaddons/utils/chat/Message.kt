package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.mixins.accessors.ChatHudAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.Text

data class Message(val message: Text, var line: ChatHudLine?, val visible: MutableList<ChatHudLine.Visible>) {
	fun remove() {
		val accessor = (MCUtils.client.inGameHud.chatHud as ChatHudAccessor)
		line?.let {
			accessor.messages.remove(line)
			line = null
		}
		visible.forEach(accessor.visibleMessages::remove)
		visible.clear()
	}
}
