package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.Timestamp
import net.minecraft.text.Text
import java.util.LinkedList
import java.util.Queue

object ChatUtils {
	private var lastSendTimestamp = Timestamp.distantPast()
	private val commandQueue: Queue<String> = LinkedList()

	fun tickCommandQueue() {
		NobaAddons.mc.player ?: return commandQueue.clear()
		sendCommand(commandQueue.poll() ?: return)
		lastSendTimestamp = Timestamp.currentTime()
	}

	private fun sendCommand(command: String) {
		NobaAddons.mc.networkHandler?.sendCommand(command)
	}
	private fun send(message: String) {
		NobaAddons.mc.networkHandler?.sendChatMessage(message)
	}
	private fun add(message: Text, overlay: Boolean) {
		NobaAddons.mc.player?.sendMessage(message, overlay)
	}

	fun queueCommand(message: String) {
		commandQueue.add(message)
	}

	fun sendMessage(message: String) {
		send(message)
	}

	fun addMessage(message: Text, prefix: Boolean = true, overlay: Boolean = false) {
		val usePrefix = if(prefix) NobaAddons.PREFIX.copy() else Text.empty()
		add(usePrefix.append(message), overlay)
	}
	fun addMessage(message: String, prefix: Boolean = true, overlay: Boolean = false) {
		addMessage(Text.of(message), prefix, overlay)
	}
}