package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.events.PacketEvents
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
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
	}

	fun sendPingPacket(sendMessage: Boolean = false) {
		if(sendMessage) sendPingMessage = true

		val client = MCUtils.client
		if(client.debugHud.shouldShowPacketSizeAndPingCharts()) return

		client.networkHandler?.sendPacket(QueryPingC2SPacket(Util.getMeasuringTimeMs()))
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		val packet = event.packet as? PingResultS2CPacket ?: return
		ping = (Timestamp(Util.getMeasuringTimeMs()) - Timestamp(packet.startTime)).inWholeMilliseconds.toInt()

		if(sendPingMessage) {
			sendPingMessage = false
			ChatUtils.addMessage(tr("nobaaddons.command.ping", "Ping: ${ping}ms"))
		}
	}
}