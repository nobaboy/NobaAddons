package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.impl.HypixelEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.handler.ClientboundPacketHandler
import net.hypixel.modapi.packet.ClientboundHypixelPacket
import net.hypixel.modapi.packet.EventPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import kotlin.jvm.optionals.getOrNull

object HypixelAPI {
	val onHypixel: Boolean
		get() = MCUtils.networkHandler?.brand?.startsWith("Hypixel BungeeCord") == true

	var location: ClientboundLocationPacket? = null
		private set

	val serverName: String?
		get() = location?.serverName

	val serverType: ServerType?
		get() = location?.serverType?.getOrNull()

	val lobbyName: String?
		get() = location?.lobbyName?.getOrNull()

	val mode: String?
		get() = location?.mode?.getOrNull()

	val map: String?
		get() = location?.map?.getOrNull()

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
}