package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext

class HelpCommand(
	private val manager: ChatCommandManager,
	private val categoryEnabled: () -> Boolean
) : ChatCommand() {
	override val enabled: Boolean get() = categoryEnabled()

	override val name: String = "help"
	override val hideFromHelp: Boolean = true

	override fun run(ctx: ChatContext) {
		val commands = manager.commands()
			.asSequence()
			.filter { it.enabled }
			.filter { !it.hideFromHelp }
			.map { it.usage }

		val commandsList = commands.joinToString(", ")
		if(commandsList.isEmpty()) return

		ctx.reply("NobaAddons > $commandsList")
		startCooldown()
	}
}