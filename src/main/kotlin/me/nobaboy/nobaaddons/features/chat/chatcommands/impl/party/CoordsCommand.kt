package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

class CoordsCommand : IChatCommand {
	override val name: String = "coords"

	override val isEnabled: Boolean
		get() = NobaConfigManager.config.chat.chatCommands.party.coords

	override fun run(ctx: ChatContext) {
		val coords = LocationUtils.playerCoords()
		HypixelCommands.partyChat(coords)
	}
}