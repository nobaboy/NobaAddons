package me.nobaboy.nobaaddons.features.chat.chatcommands

interface IChatCommand {
	val enabled: Boolean
	val name: String
	val aliases: List<String> get() = emptyList<String>()
	val usage: String get() = name
	val bypassCooldown: Boolean get() = false

	fun run(ctx: ChatContext)
}