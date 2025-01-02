package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.dm

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

class WarpMeCommand : IChatCommand {
	override val enabled: Boolean get() = config.dm.warpMe

	override val name: String = "warpme"

	override fun run(ctx: ChatContext) {
		val playerName = MCUtils.playerName ?: return
		if(ctx.user == playerName) return

		if(WarpPlayerHandler.isWarping) {
			HypixelCommands.privateChat(ctx.user, "Warp-in is on cooldown, try again later!")
			return
		}

		WarpPlayerHandler.warpPlayer(ctx.user, false, "msg ${ctx.user}")
	}
}