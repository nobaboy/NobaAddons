package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket
import net.minecraft.util.Util

object PingUtils {
	private val callbackLock = Any()
	private val pingCallbacks: MutableList<(Int) -> Unit> = mutableListOf()

	var ping: Int = 0
		private set

	init {
		Scheduler.schedule(10 * 20, repeat = true) { sendPingPacket() }
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
	}

	fun sendPingPacket() {
		val client = MCUtils.client
		if(client.debugHud.shouldShowPacketSizeAndPingCharts()) return

		client.networkHandler?.sendPacket(QueryPingC2SPacket(Util.getMeasuringTimeMs()))
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		val packet = event.packet as? PingResultS2CPacket ?: return

		val ping = (Util.getMeasuringTimeMs() - packet.startTime).toInt()
		this.ping = ping

		val callbacks = synchronized(callbackLock) {
			val callbacks = pingCallbacks.toList()
			pingCallbacks.clear()
			callbacks
		}

		callbacks.forEach { callback ->
			ErrorManager.catching("Ping callback method failed") { callback(ping) }
		}
	}

	fun requestPing(callback: (Int) -> Unit) {
		synchronized(callbackLock) {
			pingCallbacks.add(callback)
		}
		sendPingPacket()
	}
}