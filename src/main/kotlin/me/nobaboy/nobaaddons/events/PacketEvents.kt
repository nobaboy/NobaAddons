package me.nobaboy.nobaaddons.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.packet.Packet

object PacketEvents {
	val RECEIVE = EventFactory.createArrayBacked(PacketReceiveEvent::class.java) { listeners ->
		PacketReceiveEvent { packet ->
			listeners.forEach { it.onPacketReceive(packet) }
		}
	}

	val SEND = EventFactory.createArrayBacked(PacketSendEvent::class.java) { listeners ->
		PacketSendEvent { packet ->
			listeners.forEach { it.onPacketSend(packet) }
		}
	}

	fun interface PacketReceiveEvent {
		fun onPacketReceive(packet: Packet<*>)
	}

	fun interface PacketSendEvent {
		fun onPacketSend(packet: Packet<*>)
	}
}