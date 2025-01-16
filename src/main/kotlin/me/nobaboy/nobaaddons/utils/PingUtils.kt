package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket
import net.minecraft.util.Util

object PingUtils {
	private var sendPingMessage = false

	var ping: Int = 0
		private set

	init {
		Scheduler.schedule(10 * 20, repeat = true) { sendPingPacket() }
		PacketEvents.RECEIVE.register { it.packet.let { if(it is PingResultS2CPacket) onPingPacket(it) } }
	}

	fun sendPingPacket(sendMessage: Boolean = false) {
		if(sendMessage) sendPingMessage = true

		val client = MCUtils.client
		if(client.debugHud.shouldShowPacketSizeAndPingCharts()) return

		client.networkHandler?.sendPacket(QueryPingC2SPacket(Util.getMeasuringTimeMs()))
	}

	fun onPingPacket(packet: PingResultS2CPacket) {
		ping = (Timestamp(Util.getMeasuringTimeMs()) - Timestamp(packet.startTime)).inWholeMilliseconds.toInt()

		if(sendPingMessage) {
			sendPingMessage = false
			ChatUtils.addMessage(tr("nobaaddons.command.ping", "Ping: ${ping}ms"))
		}
	}
}