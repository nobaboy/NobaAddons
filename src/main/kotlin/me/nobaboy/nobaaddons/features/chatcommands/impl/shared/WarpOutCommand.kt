package me.nobaboy.nobaaddons.features.chatcommands.impl.shared

import me.nobaboy.nobaaddons.features.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

class WarpOutCommand(private var command: String, private val enabled: () -> Boolean) : IChatCommand {
	override val name: String = "warpout"

	override val usage: String = "warpout [username]"

	override val isEnabled: Boolean
		get() = enabled()

	override fun run(ctx: ChatContext) {
		if(command == "msg") command = "msg ${ctx.user()}"

		if(WarpPlayerHandler.isWarping) {
			ChatUtils.queueCommand("$command Warp-out is on cooldown, try again later!")
			return
		}

		val args = ctx.args()
		if(args.isEmpty()) {
			ChatUtils.queueCommand("$command Please provide a username.")
			return
		}

		WarpPlayerHandler.warpPlayer(args[0], true, command)
	}
}