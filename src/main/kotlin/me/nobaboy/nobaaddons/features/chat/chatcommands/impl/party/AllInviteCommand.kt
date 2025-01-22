package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import kotlin.time.Duration.Companion.seconds

class AllInviteCommand : ChatCommand(0.3.seconds) {
	override val enabled: Boolean get() = config.party.allInvite

	override val name: String = "allinvite"

	override val aliases = listOf("allinv")

	override val usage: String = "(allinvite|allinv)"

	override fun run(ctx: ChatContext) {
		if(PartyAPI.party?.isLeader != true) return
		HypixelCommands.partyAllInvite()
		startCooldown()
	}
}