package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

class CoordsCommand : ChatCommand() {
	override val enabled: Boolean get() = config.party.coords

	override val name: String = "coords"

	override fun run(ctx: ChatContext) {
		ctx.reply(LocationUtils.playerCoords())
		startCooldown()
	}
}