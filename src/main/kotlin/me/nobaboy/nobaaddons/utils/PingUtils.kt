package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.events.impl.client.WorldEvents
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket
import net.minecraft.util.Util
import kotlin.time.Duration.Companion.seconds

object PingUtils {
	private val callbackLock = Any()
	private val pingCallbacks: MutableList<(Int) -> Unit> = mutableListOf()

	var ping: Int = 0
		private set

	var currentTps: Double = 20.0
		private set

	val averageTps: Double
		get() = tickRates.toList()
			.takeIf { it.isNotEmpty() }
			?.average()
			?.roundTo(1)
			?.let { if(it >= 19.7) 20.0 else it }
			?: currentTps

	private val tickRates = TimedSet<Double>(5.seconds)
	private var previousTime = 0L

	init {
		WorldEvents.LOAD.register { reset() }
		WorldEvents.TIME_UPDATE.register(this::onWorldTimeUpdate)
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
		Scheduler.schedule(5 * 20, repeat = true) { sendPingPacket() }
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		when(val packet = event.packet) {
			is PingResultS2CPacket -> onPingResult(packet)
		}
	}

	fun sendPingPacket() {
		val client = MCUtils.client
		if(client.debugHud.shouldShowPacketSizeAndPingCharts()) return
		client.networkHandler?.sendPacket(QueryPingC2SPacket(Util.getMeasuringTimeMs()))
	}

	private fun onPingResult(packet: PingResultS2CPacket) {
		ping = (Util.getMeasuringTimeMs() - packet.startTime).toInt()

		val callbacks = synchronized(callbackLock) {
			pingCallbacks.toList().also { pingCallbacks.clear() }
		}

		callbacks.forEach { callback ->
			ErrorManager.catching("Ping callback method failed") { callback(ping) }
		}
	}

	private fun onWorldTimeUpdate(event: WorldEvents.TimeUpdate) {
		val currentTime = Util.getMeasuringTimeMs()

		if(previousTime != 0L) {
			val elapsed = (currentTime - previousTime).coerceAtLeast(1) // avoid division by 0
			val tps = (20.0 * 1000 / elapsed).coerceIn(0.0, 20.0).roundTo(1)
			currentTps = tps
			tickRates.add(tps)
		}

		previousTime = currentTime
	}

	fun requestPing(callback: (Int) -> Unit) {
		synchronized(callbackLock) {
			pingCallbacks.add(callback)
		}
		sendPingPacket()
	}

	private fun reset() {
		ping = 0
		currentTps = 20.0
		tickRates.clear()
		previousTime = 0L
	}
}