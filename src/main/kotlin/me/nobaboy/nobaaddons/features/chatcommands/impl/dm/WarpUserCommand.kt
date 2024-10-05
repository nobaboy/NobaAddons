package me.nobaboy.nobaaddons.features.chatcommands.impl.dm

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.utils.Utils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

class WarpUserCommand : IChatCommand {
    override val name: String = "warpme"

    override val isEnabled: Boolean
        get() = NobaConfigManager.get().chatCommands.dm.warpMe

    override fun run(ctx: ChatContext) {
        val playerName = Utils.getPlayerName() ?: return
        if (ctx.user() == playerName) return

        if (WarpPlayerHandler.isWarping) {
            HypixelCommands.privateChat(ctx.user(), "Warp-in is on cooldown, try again later!")
            return
        }

        WarpPlayerHandler.warpPlayer(ctx.user(), false, "msg ${ctx.user()}")
    }
}