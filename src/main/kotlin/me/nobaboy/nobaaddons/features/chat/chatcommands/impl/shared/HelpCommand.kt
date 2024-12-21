package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

class HelpCommand(
	private val manager: ChatCommandManager,
	private var command: String,
	private val categoryEnabled: () -> Boolean
) : IChatCommand {
	override val enabled: Boolean get() = categoryEnabled()

	override val name: String = "help"

	override fun run(ctx: ChatContext) {
		if(command == "msg") command = "msg ${ctx.user}"
		val commands = manager.getCommands(true).map { it.usage }
		val commandsList = commands.joinToString(", ")

		ChatUtils.queueCommand("$command NobaAddons > [! ? .] | $commandsList")
	}
}