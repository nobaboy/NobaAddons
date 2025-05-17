package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.hypixel.HypixelCommands
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import kotlin.time.Duration.Companion.seconds

class AllInviteCommand : AbstractPartyChatCommand(0.3.seconds) {
	override val enabled: Boolean get() = config.party.allInvite
	override val requireClientPlayerIs = ClientboundPartyInfoPacket.PartyRole.LEADER

	override val name: String = "allinvite"
	override val aliases = listOf("allinv")
	override val usage: String = "(allinvite|allinv)"

	override suspend fun run(ctx: ChatContext) {
		HypixelCommands.partyAllInvite()
		startCooldown()
	}
}