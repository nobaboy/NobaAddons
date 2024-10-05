package me.nobaboy.nobaaddons.features.chatcommands.impl.party

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.HypixelCommands

class CoordsCommand : IChatCommand {
    override val name: String = "coords"

    override val isEnabled: Boolean
        get() = NobaConfigManager.get().chatCommands.party.coords

    override fun run(ctx: ChatContext) {
        val player = NobaAddons.mc.player ?: return
        val (x, y, z) = listOf(player.x, player.y, player.z).map { it.toInt() }
        HypixelCommands.partyChat("x: $x, y: $y, z: $z")
    }
}