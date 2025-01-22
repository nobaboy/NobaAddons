package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import kotlin.time.Duration.Companion.seconds

class WarpOutCommand(private var command: String, private val categoryEnabled: () -> Boolean) : ChatCommand(3.seconds) {
	override val enabled: Boolean get() = categoryEnabled()

	override val name: String = "warpout"

	override val usage: String = "warpout [username]"

	override fun run(ctx: ChatContext) {
		if(command == "msg") command = "msg ${ctx.user}"

		if(WarpPlayerHandler.isWarping) {
			ChatUtils.queueCommand("$command Warp-out is on cooldown, try again later!")
			return
		}

		val args = ctx.args
		if(args.isEmpty()) {
			ChatUtils.queueCommand("$command Please provide a username.")
			return
		}

		WarpPlayerHandler.warpPlayer(args[0], true, command)
		startCooldown()
	}
}