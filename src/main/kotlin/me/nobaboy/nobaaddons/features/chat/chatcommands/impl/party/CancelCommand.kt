package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.IChatCommand

class CancelCommand : IChatCommand {
	override val enabled: Boolean get() = config.party.warp

	override val name: String = "cancel"

	override val bypassCooldown: Boolean = true

	override fun run(ctx: ChatContext) {
		WarpCommand.cancel = true
	}
}