package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.AbstractEvent
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.network.packet.Packet

class PacketEvents private constructor() {
	/**
	 * Event invoked before a [Packet] sent by the server is handled.
	 */
	data class EarlyReceive(val packet: Packet<*>) : AbstractEvent() {
		companion object : EventDispatcher<EarlyReceive>()
	}

	/**
	 * Event invoked after a [Packet] sent by the server has been handled.
	 */
	data class PostReceive(val packet: Packet<*>) : AbstractEvent() {
		companion object : EventDispatcher<PostReceive>()
	}

	/**
	 * Event invoked when a [Packet] is sent to the server.
	 */
	data class Send(val packet: Packet<*>) : AbstractEvent() {
		companion object : EventDispatcher<Send>()
	}
}