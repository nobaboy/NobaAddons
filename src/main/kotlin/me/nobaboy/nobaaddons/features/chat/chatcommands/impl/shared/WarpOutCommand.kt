package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import kotlin.time.Duration.Companion.seconds

class WarpOutCommand(private val categoryEnabled: () -> Boolean) : ChatCommand(3.seconds) {
	override val enabled: Boolean get() = categoryEnabled()

	override val name: String = "warpout"
	override val usage: String = "warpout [username]"

	override suspend fun run(ctx: ChatContext) {
		if(WarpPlayerHandler.isWarping) {
			ctx.reply("I'm already warping someone else, try again in a minute!")
			return
		}

		val target = ctx.args.getOrNull(0)
		if(target == null) {
			ctx.reply("Please provide a username.")
			return
		}
		if(target.equals(MCUtils.playerName, ignoreCase = true)) return

		WarpPlayerHandler.warpPlayer(target, true, ctx.replyCommand)
		startCooldown()
	}
}