package me.nobaboy.nobaaddons.commands

import me.nobaboy.nobaaddons.commands.annotations.Command
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.commands.annotations.Group
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

@Suppress("unused")
@Group("internal")
object InternalCommands {
	@Command
	fun action(ctx: Context, id: String) {
		ChatUtils.processClickAction(id)
	}

	@Command
	fun copyError(ctx: Context, id: String) {
		ErrorManager.copyError(id)
	}
}