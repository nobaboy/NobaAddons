package me.nobaboy.nobaaddons.features.chatcommands.impl.party

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chatcommands.IChatCommand
import me.nobaboy.nobaaddons.utils.LocationUtils.playerCoords
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

class CoordsCommand : IChatCommand {
	override val name: String = "coords"

	override val isEnabled: Boolean
		get() = NobaConfigManager.get().chatCommands.party.coords

	override fun run(ctx: ChatContext) {
		val coords = playerCoords()
		HypixelCommands.partyChat(coords)
	}
}