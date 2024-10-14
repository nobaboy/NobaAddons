package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.util.Window
import net.minecraft.network.packet.Packet

object MCUtils {
	fun client(): MinecraftClient = MinecraftClient.getInstance()

	fun player(): ClientPlayerEntity? = client().player
	fun playerName(): String = player()?.name.toString()

	fun window(): Window = client().window

	fun sendPacket(packet: Packet<*>) {
		if(client().networkHandler?.connection == null) {
			NobaAddons.LOGGER.error("Tried to send a packet {} but connection was null", packet::class.java.simpleName)
			return
		}

		client().networkHandler!!.sendPacket(packet)
	}
}