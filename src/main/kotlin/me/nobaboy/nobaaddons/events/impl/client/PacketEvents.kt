package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.network.packet.Packet

object PacketEvents {
	/**
	 * Event invoked before a [Packet] sent by the server is handled.
	 */
	@JvmField val EARLY_RECEIVE = EventDispatcher<EarlyReceive>()

	/**
	 * Event invoked after a [Packet] sent by the server has been handled.
	 */
	@JvmField val RECEIVE = EventDispatcher<Receive>()

	/**
	 * Event invoked when a [Packet] is sent to the server.
	 */
	@JvmField val SEND = EventDispatcher<Send>()

	data class EarlyReceive(val packet: Packet<*>) : Event()
	data class Receive(val packet: Packet<*>) : Event()
	data class Send(val packet: Packet<*>) : Event()
}