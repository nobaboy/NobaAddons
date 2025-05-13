package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.utils.chat.ChatUtils

class ChatContext(
	val source: ChatCommandSource,
	val user: String,
	val command: String,
	val args: List<String>,
	val fullMessage: String,
) {
	val replyCommand: String get() = when(source) {
		ChatCommandSource.GUILD -> "gc"
		ChatCommandSource.PARTY -> "pc"
		ChatCommandSource.MESSAGE -> "msg $user"
	}

	fun reply(message: String) {
		ChatUtils.queueCommand("$replyCommand $message")
	}

	enum class ChatCommandSource {
		PARTY,
		GUILD,
		MESSAGE;
	}
}