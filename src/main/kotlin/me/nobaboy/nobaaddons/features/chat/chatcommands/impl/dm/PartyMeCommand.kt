package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.dm

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.hypixel.HypixelCommands
import me.nobaboy.nobaaddons.utils.mc.MCUtils

class PartyMeCommand : ChatCommand() {
	override val enabled: Boolean get() = config.dm.partyMe

	override val name: String = "partyme"
	override val aliases = listOf("pme")

	override suspend fun run(ctx: ChatContext) {
		val playerName = MCUtils.playerName ?: return
		if(ctx.user == playerName) return

		HypixelCommands.partyInvite(ctx.user)
		startCooldown()
	}
}