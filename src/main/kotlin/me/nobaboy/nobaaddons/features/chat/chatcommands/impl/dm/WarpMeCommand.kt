package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.dm

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import kotlin.time.Duration.Companion.seconds

class WarpMeCommand : ChatCommand(3.seconds) {
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
		startCooldown()
	}
}