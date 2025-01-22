package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext

class CancelCommand : ChatCommand() {
	override val enabled: Boolean get() = config.party.warp

	override val name: String = "cancel"
	override val hideFromHelp: Boolean = true

	override fun run(ctx: ChatContext) {
		WarpCommand.cancel = true
	}
}