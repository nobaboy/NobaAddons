package me.nobaboy.nobaaddons.events.impl

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket

// TODO should this just be a singular event class, something like ServerChangeEvent
object HypixelEvents {
	val SERVER_CHANGE = EventDispatcher<ServerChange>()

	data class ServerChange(val packet: ClientboundLocationPacket) : Event
}