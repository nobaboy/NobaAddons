package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.CooldownTickEvent
import me.nobaboy.nobaaddons.utils.CooldownTickEvent.Companion.ticks
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting
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
			(MCUtils.networkHandler ?: return).sendCommand(commandQueue.poll() ?: return)
			cooldownManager.startCooldown(20.ticks)
		}
	}

	fun queueCommand(message: String) {
		commandQueue.add(message)
	}

	fun sendChatAsPlayer(message: String) {
		MCUtils.networkHandler?.sendChatMessage(message)
	}

	fun addMessage(message: Text, prefix: Boolean = true, overlay: Boolean = false) {
		val text = buildText {
			if(prefix) {
				append(NobaAddons.PREFIX)
				formatted(Formatting.AQUA)
			}
			append(message)
		}
		MCUtils.player?.sendMessage(text, overlay)
	}

	fun addMessage(message: String, prefix: Boolean = true, overlay: Boolean = false) {
		addMessage(Text.literal(message), prefix, overlay)
	}
}