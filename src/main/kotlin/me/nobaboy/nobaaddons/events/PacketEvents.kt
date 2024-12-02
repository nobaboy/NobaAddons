package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.network.packet.Packet

object PacketEvents {
	@JvmField
	val EARLY_RECEIVE = EventDispatcher<EarlyReceive>()

	@JvmField
	val RECEIVE = EventDispatcher<Receive>()

	@JvmField
	val SEND = EventDispatcher<Send>()

	data class EarlyReceive(val packet: Packet<*>)
	data class Receive(val packet: Packet<*>)
	data class Send(val packet: Packet<*>)
}