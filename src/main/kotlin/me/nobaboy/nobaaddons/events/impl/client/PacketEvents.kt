package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.network.packet.Packet

object PacketEvents {
	@JvmField val POST_RECEIVE = Receive.Companion
	@JvmField val SEND = Send.Companion

	/**
	 * Event invoked before a [Packet] sent by the server is handled.
	 */
	data class EarlyReceive(val packet: Packet<*>) : Event() {
		companion object : EventDispatcher<EarlyReceive>()
	}

	/**
	 * Event invoked after a [Packet] sent by the server has been handled.
	 */
	data class Receive(val packet: Packet<*>) : Event() {
		companion object : EventDispatcher<Receive>()
	}

	/**
	 * Event invoked when a [Packet] is sent to the server.
	 */
	data class Send(val packet: Packet<*>) : Event() {
		companion object : EventDispatcher<Send>()
	}
}