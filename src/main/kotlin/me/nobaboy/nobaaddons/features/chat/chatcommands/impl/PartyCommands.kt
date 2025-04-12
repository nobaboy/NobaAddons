package me.nobaboy.nobaaddons.features.chat.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.AllInviteCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CancelCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CoordsCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.JoinInstanceCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.TransferCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.WarpCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting

object PartyCommands : ChatCommandManager() {
	private val config get() = NobaConfig.chat.chatCommands.party

	override val source: ChatContext.ChatCommandSource = ChatContext.ChatCommandSource.PARTY
	override val enabled: Boolean get() = config.enabled && onHypixel()
	override val pattern by Repo.regex(
		"chat_commands.party",
		"^Party > (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_ ]+)?"
	)

	init {
		register(HelpCommand(this, config::help))
		register(TransferCommand())
		register(AllInviteCommand())
		register(WarpCommand())
		register(CancelCommand())
		register(CoordsCommand())
		register(JoinInstanceCommands())
	}

	fun init() {
		ChatMessageEvents.CHAT.register { (message) ->
			processMessage(message.string.cleanFormatting())
		}
	}
}