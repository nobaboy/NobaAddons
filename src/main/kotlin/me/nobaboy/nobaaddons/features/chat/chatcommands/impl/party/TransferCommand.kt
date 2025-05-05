package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket

class TransferCommand : AbstractPartyChatCommand() {
	override val enabled: Boolean get() = config.party.transfer
	override val requireClientPlayerIs = ClientboundPartyInfoPacket.PartyRole.LEADER

	override val name: String = "transfer"

	override val aliases = listOf("ptme", "pt")

	override val usage: String = "(transfer|pt) [optional: username], ptme"

	override suspend fun run(ctx: ChatContext) {
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