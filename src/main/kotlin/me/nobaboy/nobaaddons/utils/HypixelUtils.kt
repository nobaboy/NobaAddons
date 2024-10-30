package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPingPacket

object HypixelUtils {
	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.serverInfo?.address?.endsWith(".hypixel.net") == true

	private var lastPing = Timestamp.distantPast()
	var ping: Int = 0
		private set

	init {
		Scheduler.schedule(60 * 20, repeat = true) { sendPingPacket() }
		HypixelModAPI.getInstance().listen<ClientboundPingPacket>(this::onPingPacket)
	}

	fun sendPingPacket() {
		if(!onHypixel) return
		HypixelModAPI.getInstance().sendPacket(ServerboundPingPacket())
		lastPing = Timestamp.currentTime()
	}

	fun onPingPacket(ignored: ClientboundPingPacket) {
		if(lastPing == Timestamp.distantPast()) return
		ping = (Timestamp.currentTime() - lastPing).inWholeMilliseconds.toInt()
	}
}