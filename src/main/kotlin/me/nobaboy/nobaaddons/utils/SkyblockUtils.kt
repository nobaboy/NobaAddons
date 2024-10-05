package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.data.IslandType
import net.hypixel.data.type.GameType
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import kotlin.jvm.optionals.getOrNull

// TODO: Implement island zones (like Bank, Carnival, etc.)
object SkyblockUtils {
    val inSkyblock: Boolean
        get() = Utils.onHypixel && currentGame == GameType.SKYBLOCK
    var currentIsland: IslandType = IslandType.UNKNOWN
        private set

    var currentGame: ServerType? = null
        private set

    fun isIn(island: IslandType): Boolean = currentIsland == island

    fun onLocationPacket(packet: ClientboundLocationPacket) {
        currentGame = packet.serverType.getOrNull()
        currentIsland == packet.mode.map(IslandType::getIslandType).orElse(IslandType.UNKNOWN)
    }
}