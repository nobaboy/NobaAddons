package me.nobaboy.nobaaddons.features.chatcommands.impl.party

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

class AllInviteCommand : IChatCommand {
	override val name: String = "allinvite"

	override val aliases = mutableListOf("allinv")

	override val usage: String = "(allinvite|allinv)"

	override val isEnabled: Boolean
		get() = NobaConfigManager.get().chatCommands.party.allInvite

	override fun run(ctx: ChatContext) {
		if(!PartyAPI.isLeader()) return
		HypixelCommands.partyAllInvite()
	}
}