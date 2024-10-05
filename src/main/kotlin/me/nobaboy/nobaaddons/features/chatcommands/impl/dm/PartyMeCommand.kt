package me.nobaboy.nobaaddons.features.chatcommands.impl.dm

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.HypixelCommands
import me.nobaboy.nobaaddons.utils.Utils

class PartyMeCommand : IChatCommand {
    override val name: String = "partyme"

    override val aliases: MutableList<String> = mutableListOf("pme")

    override val isEnabled: Boolean
        get() = NobaConfigManager.get().chatCommands.dm.partyMe

    override fun run(ctx: ChatContext) {
        val playerName = Utils.getPlayerName() ?: return
        if (ctx.user() == playerName) return

        HypixelCommands.partyInvite(ctx.user())
    }
}