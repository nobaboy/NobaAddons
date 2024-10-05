package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
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
        NobaAddons.mc.networkHandler?.sendCommand(command) ?: return
    }
    private fun send(message: String) {
        NobaAddons.mc.networkHandler?.sendChatMessage(message) ?: return
    }
    private fun add(message: Text) {
        NobaAddons.mc.player?.sendMessage(message) ?: return
    }

    fun queueCommand(message: String) {
        commandQueue.add(message)
    }

    fun sendMessage(message: String) {
        send(message)
    }

    fun addMessage(message: Text, prefix: Boolean = true) {
        val usePrefix = if (prefix) NobaAddons.PREFIX.copy() else Text.empty()
        add(usePrefix.append(message))
    }
    fun addMessage(message: String, prefix: Boolean = true) {
        addMessage(Text.of(message), prefix)
    }
}