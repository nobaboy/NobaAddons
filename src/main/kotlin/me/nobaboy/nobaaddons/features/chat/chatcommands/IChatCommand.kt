package me.nobaboy.nobaaddons.features.chat.chatcommands

interface IChatCommand {
	val name: String
	val usage: String get() = name
	val isEnabled: Boolean get() = true
	val aliases: List<String> get() = emptyList<String>()
	val bypassCooldown: Boolean get() = false

	fun run(ctx: ChatContext)
}