package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.dm

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import kotlin.time.Duration.Companion.seconds

class WarpMeCommand : ChatCommand(3.seconds) {
	override val enabled: Boolean get() = config.dm.warpMe
	override val name: String = "warpme"

	override suspend fun run(ctx: ChatContext) {
		val playerName = MCUtils.playerName ?: return
		if(ctx.user == playerName) return

		if(WarpPlayerHandler.isWarping) {
			ctx.reply("I'm already warping someone else, try again in a minute!")
			return
		}

		WarpPlayerHandler.warpPlayer(ctx.user, false, "msg ${ctx.user}")
		startCooldown()
	}
}