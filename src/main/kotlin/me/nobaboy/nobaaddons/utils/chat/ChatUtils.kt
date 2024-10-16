package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.Timestamp
import net.minecraft.text.Text
import java.util.LinkedList
import java.util.Queue

object ChatUtils {
	private var lastSendTimestamp = Timestamp.distantPast()
	private val commandQueue: Queue<String> = LinkedList()

	// FIXME: I feel like it's waiting 1 second before it actually sends a command rather than adding it to a queue
	fun tickCommandQueue() {
		MCUtils.player ?: return commandQueue.clear()
		sendCommand(commandQueue.poll() ?: return)
		lastSendTimestamp = Timestamp.currentTime()
	}

	private fun send(message: String) {
		MCUtils.networkHandler?.sendChatMessage(message)
	}
	private fun add(message: Text, overlay: Boolean) {
		MCUtils.player?.sendMessage(message, overlay)
	}

	/**
	 * Ideally use the command queuing system over sending the command instantly but if
	 * you must then no one's stopping you.
	 * @see queueCommand
	 */
	fun sendCommand(command: String) {
		MCUtils.networkHandler?.sendCommand(command)
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