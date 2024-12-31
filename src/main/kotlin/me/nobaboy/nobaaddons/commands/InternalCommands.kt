package me.nobaboy.nobaaddons.commands

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

@Suppress("unused")
@Group("internal")
object InternalCommands {
	@Command
	fun action(id: String) {
		ChatUtils.processClickAction(id)
	}

	@Command
	fun copyError(id: String) {
		ErrorManager.copyError(id)
	}
}