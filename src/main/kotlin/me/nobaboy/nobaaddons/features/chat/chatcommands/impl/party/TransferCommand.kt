package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.hypixel.HypixelCommands

class TransferCommand : ChatCommand() {
	override val enabled: Boolean get() = config.party.transfer

	override val name: String = "transfer"

	override val aliases = listOf("ptme", "pt")

	override val usage: String = "(transfer|pt) [optional: username], ptme"

	override fun run(ctx: ChatContext) {
		if(PartyAPI.party?.isLeader != true) return

		if(!ctx.command.equals("ptme", ignoreCase = true)) {
			val player = if(ctx.args.isEmpty()) ctx.user else ctx.args[0]
			HypixelCommands.partyTransfer(player)
			return
		}

		if(ctx.user == MCUtils.playerName) return
		HypixelCommands.partyTransfer(ctx.user)
		startCooldown()
	}
}