package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.config.NobaConfig

interface IChatCommand {
	val config get() = NobaConfig.INSTANCE.chat.chatCommands

	val enabled: Boolean
	val name: String
	val aliases: List<String> get() = emptyList()
	val usage: String get() = name
	val bypassCooldown: Boolean get() = false

	fun run(ctx: ChatContext)
}