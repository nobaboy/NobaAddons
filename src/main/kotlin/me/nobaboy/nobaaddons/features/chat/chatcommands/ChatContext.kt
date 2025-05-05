package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.utils.mc.chat.ChatUtils

class ChatContext(
	val source: ChatCommandSource,
	val user: String,
	val command: String,
	val args: List<String>,
	val fullMessage: String,
) {
	fun reply(message: String) {
		val command = when(source) {
			ChatCommandSource.GUILD -> "gc"
			ChatCommandSource.PARTY -> "pc"
			ChatCommandSource.MESSAGE -> "msg $user"
		}
		ChatUtils.queueCommand("$command $message")
	}

	enum class ChatCommandSource {
		PARTY,
		GUILD,
		MESSAGE;
	}
}