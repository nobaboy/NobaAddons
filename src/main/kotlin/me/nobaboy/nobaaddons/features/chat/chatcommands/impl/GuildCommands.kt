package me.nobaboy.nobaaddons.features.chat.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpOutCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.repo.Repo.fromRepo

object GuildCommands : ChatCommandManager() {
	private val config get() = NobaConfig.chat.chatCommands.guild

	override val source: ChatContext.ChatCommandSource = ChatContext.ChatCommandSource.GUILD
	override val enabled: Boolean get() = config.enabled && onHypixel()
	override val pattern by Regex("^Guild > (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+)(?<grank> \\[[A-z0-9 ]+])?: [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_ ]+)?").fromRepo("chat_commands.guild")

	init {
		register(HelpCommand(this, config::help))
		register(WarpOutCommand("gc", config::warpOut))
	}

	fun init() {
		ChatMessageEvents.CHAT.register {
			val message = it.cleaned

			if(WarpPlayerHandler.isWarping) {
				WarpPlayerHandler.onChatMessage(message)
				return@register
			}

			processMessage(message)
		}
	}
}