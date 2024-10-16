package me.nobaboy.nobaaddons.utils

import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPingPacket

object Utils {
	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.serverInfo?.address?.endsWith(".hypixel.net") == true

	private var lastPing = Timestamp.distantPast()
	var ping: Int = 0
		private set

	fun sendPingPacket() {
		if(!onHypixel) return
		HypixelModAPI.getInstance().sendPacket(ServerboundPingPacket())
		lastPing = Timestamp.currentTime()
	}

	fun onPingPacket(ignored: ClientboundPingPacket) {
		if (lastPing == Timestamp.distantPast()) return
		ping = (Timestamp.currentTime() - lastPing).inWholeMilliseconds.toInt()
	}
}