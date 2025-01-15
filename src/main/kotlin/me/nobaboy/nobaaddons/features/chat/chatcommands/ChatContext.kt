package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

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
		if(user == MCUtils.playerName) {
			Scheduler.schedule(5) { ChatUtils.queueCommand("$command $message") }
		} else {
			ChatUtils.queueCommand("$command $message")
		}
	}

	enum class ChatCommandSource {
		PARTY,
		GUILD,
		MESSAGE;
	}
}