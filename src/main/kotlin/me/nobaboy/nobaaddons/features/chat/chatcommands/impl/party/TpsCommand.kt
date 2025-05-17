package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.mc.PingUtils

class TpsCommand : ChatCommand() {
	override val enabled: Boolean get() = config.party.tps
	override val name: String = "tps"

	override suspend fun run(ctx: ChatContext) {
		ctx.reply("TPS: ${PingUtils.currentTps}")
		startCooldown()
	}
}