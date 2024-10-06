package me.nobaboy.nobaaddons.features.chatcommands.impl.party

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chatcommands.IChatCommand

class CancelCommand : IChatCommand {
	override val name: String = "cancel"

	override val isEnabled: Boolean
		get() = NobaConfigManager.get().chatCommands.party.warp

	override val bypassCooldown: Boolean = true

	override fun run(ctx: ChatContext) {
		WarpCommand.cancel = true
	}
}