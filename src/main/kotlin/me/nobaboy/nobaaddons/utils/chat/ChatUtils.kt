package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.CooldownTickEvent
import me.nobaboy.nobaaddons.utils.CooldownTickEvent.Companion.ticks
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.util.LinkedList
import java.util.Queue

object ChatUtils {
	private val commandQueue: Queue<String> = LinkedList()

	init {
		CooldownTickEvent.EVENT.register(CommandQueue)
	}

	private object CommandQueue : CooldownTickEvent {
		override fun onTick(client: MinecraftClient) {
			if(MCUtils.player == null) {
				commandQueue.clear()
				return
			}
			sendCommand(commandQueue.poll() ?: return)
			cooldownManager.startCooldown(20.ticks)
		}
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