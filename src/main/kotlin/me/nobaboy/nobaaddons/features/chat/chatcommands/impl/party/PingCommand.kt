package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.mc.PingUtils

class PingCommand : ChatCommand() {
	override val enabled: Boolean get() = config.party.ping
	override val name: String = "ping"

	override suspend fun run(ctx: ChatContext) {
		PingUtils.requestPing { ctx.reply("Ping: ${it}ms") }
		startCooldown()
	}
}