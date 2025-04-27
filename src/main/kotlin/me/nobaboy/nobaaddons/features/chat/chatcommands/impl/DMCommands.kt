package me.nobaboy.nobaaddons.features.chat.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.dm.PartyMeCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.dm.WarpMeCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpOutCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns

object DMCommands : ChatCommandManager() {
	private val config get() = NobaConfig.chat.chatCommands.dm

	override val source: ChatContext.ChatCommandSource = ChatContext.ChatCommandSource.MESSAGE
	override val enabled: Boolean get() = config.enabled && onHypixel()
	override val pattern by Regex("^From ${CommonPatterns.PLAYER_NAME_WITH_RANK_STRING}: [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_ ]+)?").fromRepo("chat_commands.dm")

	init {
		register(HelpCommand(this, config::help))
		register(WarpOutCommand("msg", config::warpOut))
		register(WarpMeCommand())
		register(PartyMeCommand())
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