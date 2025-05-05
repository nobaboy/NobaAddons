package me.nobaboy.nobaaddons.features.chat.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpOutCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.WarpPlayerHandler
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns

object GuildCommands : ChatCommandManager() {
	private val config get() = NobaConfig.chat.chatCommands.guild

	override val source: ChatContext.ChatCommandSource = ChatContext.ChatCommandSource.GUILD
	override val enabled: Boolean get() = config.enabled && onHypixel()
	override val pattern by Regex("^Guild > ${CommonPatterns.PLAYER_NAME_WITH_RANK_STRING}(?: \\[[A-z0-9 ]+])?: [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_ ]+)?").fromRepo("chat_commands.guild")

	init {
		register(HelpCommand(this, config::help))
		register(WarpOutCommand("gc", config::warpOut))
	}
}