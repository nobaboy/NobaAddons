package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.dm

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

class PartyMeCommand : IChatCommand {
	override val enabled: Boolean get() = NobaConfigManager.config.chat.chatCommands.dm.partyMe

	override val name: String = "partyme"

	override val aliases = listOf("pme")

	override fun run(ctx: ChatContext) {
		val playerName = MCUtils.playerName ?: return
		if(ctx.user == playerName) return

		HypixelCommands.partyInvite(ctx.user)
	}
}