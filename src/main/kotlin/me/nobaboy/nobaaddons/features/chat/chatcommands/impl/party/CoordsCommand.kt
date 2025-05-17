package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.mc.LocationUtils
import kotlin.time.Duration.Companion.seconds

class CoordsCommand : ChatCommand(0.3.seconds) {
	override val enabled: Boolean get() = config.party.coords
	override val name: String = "coords"

	override suspend fun run(ctx: ChatContext) {
		ctx.reply(LocationUtils.playerCoords())
		startCooldown()
	}
}