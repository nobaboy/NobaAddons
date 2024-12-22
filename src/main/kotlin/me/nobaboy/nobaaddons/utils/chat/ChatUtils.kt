package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.events.CooldownTickEvent
import me.nobaboy.nobaaddons.events.CooldownTickEvent.Companion.ticks
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.LinkedList
import java.util.Queue

object ChatUtils {
	private val commandQueue: Queue<String> = LinkedList()

	init {
		CooldownTickEvent.EVENT.register(this::processCommandQueue)
	}

	private fun processCommandQueue(event: CooldownTickEvent) {
		if(MCUtils.player == null) {
			commandQueue.clear()
			return
		}
		(MCUtils.networkHandler ?: return).sendCommand(commandQueue.poll() ?: return)
		event.cooldownManager.startCooldown(20.ticks)
	}

	fun queueCommand(message: String) {
		commandQueue.add(message)
	}

	fun sendChatAsPlayer(message: String) {
		MCUtils.networkHandler?.sendChatMessage(message)
	}

	fun addMessage(message: Text, prefix: Boolean = true, overlay: Boolean = false, color: Formatting? = Formatting.GRAY) {
		val text = buildText {
			if(prefix) {
				append(NobaAddons.PREFIX)
				color?.let { formatted(it) }
			}
			append(message)
		}
		MCUtils.player?.sendMessage(text, overlay)
	}

	fun addMessage(message: String, prefix: Boolean = true, overlay: Boolean = false, color: Formatting? = Formatting.GRAY) {
		addMessage(Text.literal(message), prefix, overlay, color)
	}

	inline fun addMessage(prefix: Boolean = true, overlay: Boolean = false, color: Formatting? = Formatting.GRAY, builder: MutableText.() -> Unit) {
		addMessage(Text.empty().apply(builder), prefix, overlay, color)
	}
}