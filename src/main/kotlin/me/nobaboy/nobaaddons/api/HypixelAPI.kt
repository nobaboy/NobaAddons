package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.impl.HypixelEvents
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.handler.ClientboundPacketHandler
import net.hypixel.modapi.packet.ClientboundHypixelPacket
import net.hypixel.modapi.packet.EventPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import kotlin.jvm.optionals.getOrNull

object HypixelAPI {
	private lateinit var location: ClientboundLocationPacket

	val locationOrNull: ClientboundLocationPacket?
		get() = if (::location.isInitialized) location else null

	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.brand?.startsWith("Hypixel BungeeCord") == true

	val serverName: String?
		get() = locationOrNull?.serverName

	val serverType: ServerType?
		get() = locationOrNull?.serverType?.getOrNull()

	val lobbyName: String?
		get() = locationOrNull?.lobbyName?.getOrNull()

	val mode: String?
		get() = locationOrNull?.mode?.getOrNull()

	val map: String?
		get() = locationOrNull?.map?.getOrNull()

	init {
		HypixelModAPI.getInstance().subscribeToEvent<ClientboundLocationPacket>()
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket>(this::onLocationPacket)
	}

	private fun onLocationPacket(packet: ClientboundLocationPacket) {
		val newServer = packet.serverName
		if(newServer == serverName) return

		HypixelEvents.SERVER_CHANGE.dispatch(HypixelEvents.ServerChange(packet))
		location = packet
	}

	inline fun <reified T : EventPacket> HypixelModAPI.subscribeToEvent() {
		this.subscribeToEventPacket(T::class.java)
	}

	inline fun <reified T : ClientboundHypixelPacket> HypixelModAPI.listen(handler: ClientboundPacketHandler<T>) {
		this.createHandler(T::class.java, handler)
	}

	class PacketListener<T : ClientboundHypixelPacket>(val packet: Class<ClientboundHypixelPacket>) {
		private var registered = false
		val waiting = mutableListOf<(T) -> Unit>()

		@Suppress("UNCHECKED_CAST")
		@Synchronized
		fun register() {
			if(registered) return
			HypixelModAPI.getInstance().createHandler(packet) { received ->
				waiting.removeIf {
					runCatching { it(received as T) }.onFailure {
						ErrorManager.logError("Mod API packet listener threw an unhandled error", it)
					}
					true
				}
			}
			registered = true
		}
	}
}