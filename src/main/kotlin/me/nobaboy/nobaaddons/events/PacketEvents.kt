package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.network.packet.Packet

object PacketEvents {
	data class Receive(val packet: Packet<*>)
	data class Send(val packet: Packet<*>)

	@JvmField
	val RECEIVE = EventDispatcher<Receive>()

	@JvmField
	val SEND = EventDispatcher<Send>()
}