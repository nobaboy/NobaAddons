package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.PingUtils

// TODO add a !tps command to go with this; will require building out functionality to estimate server tps
// TODO add this to an all chat command manager
class PingCommand(private val categoryEnabled: () -> Boolean) : ChatCommand() {
	override val enabled: Boolean get() = categoryEnabled()
	override val name: String = "ping"

	override suspend fun run(ctx: ChatContext) {
		PingUtils.requestPing { ctx.reply("Ping: ${it}ms") }
	}
}