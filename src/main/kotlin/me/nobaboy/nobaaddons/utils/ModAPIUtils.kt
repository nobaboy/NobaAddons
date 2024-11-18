package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.handler.ClientboundPacketHandler
import net.hypixel.modapi.packet.ClientboundHypixelPacket
import net.hypixel.modapi.packet.EventPacket
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPingPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPlayerInfoPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundVersionedPacket
import java.lang.IllegalArgumentException

object ModAPIUtils {
	val oneOffListeners: MutableMap<Class<ClientboundHypixelPacket>, PacketListener<*>> = mutableMapOf()

	class PacketListener<T : ClientboundHypixelPacket>(val packet: Class<ClientboundHypixelPacket>) {
		private var registered = false
		val waiting = mutableListOf<(T) -> Unit>()

		@Suppress("UNCHECKED_CAST")
		@Synchronized
		fun register() {
			if(registered) return
			HypixelModAPI.getInstance().createHandler(packet) { received ->
				waiting.removeIf {
					runCatching { it(received as T) }.onFailure { NobaAddons.LOGGER.error("Failed to call packet listener", it) }
					true
				}
			}
			registered = true
		}
	}

	inline fun <reified T : EventPacket> HypixelModAPI.subscribeToEvent() {
		this.subscribeToEventPacket(T::class.java)
	}

	inline fun <reified T : ClientboundHypixelPacket> HypixelModAPI.listen(handler: ClientboundPacketHandler<T>) {
		this.createHandler(T::class.java, handler)
	}

	@Suppress("UNCHECKED_CAST")
	inline fun <reified T : ClientboundHypixelPacket> HypixelModAPI.request(packet: ServerboundVersionedPacket, noinline callback: (T) -> Unit) {
		oneOffListeners
			.computeIfAbsent(T::class.java as Class<ClientboundHypixelPacket>) { PacketListener<T>(it).also { it.register() } }
			.waiting.add(callback as (ClientboundHypixelPacket) -> Unit)
		sendPacket(packet)
	}

	inline fun <reified T : ClientboundHypixelPacket> HypixelModAPI.request(noinline callback: (T) -> Unit) {
		val toSend = when(T::class) {
			ClientboundPingPacket::class -> ServerboundPingPacket()
			ClientboundPartyInfoPacket::class -> ServerboundPartyInfoPacket()
			ClientboundPlayerInfoPacket::class -> ServerboundPlayerInfoPacket()
			else -> throw IllegalArgumentException("${T::class.java} does not have a serverbound packet")
		}
		request<T>(toSend, callback)
	}
}
