package me.nobaboy.nobaaddons.utils.enums

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class AnnounceChannel : NameableEnum {
	ALL,
	PARTY,
	GUILD,
	;

	fun send(message: String) {
		when(this) {
			ALL -> HypixelCommands.allChat(message)
			PARTY -> if(PartyAPI.party != null) HypixelCommands.partyChat(message)
			// TODO: some how figure out if the user is in a guild
			GUILD -> HypixelCommands.guildChat(message)
		}
	}

	override fun getDisplayName(): Text = when(this) {
		ALL -> tr("nobaaddons.label.announceChannel.all", "All")
		PARTY -> tr("nobaaddons.label.announceChannel.party", "Party")
		GUILD -> tr("nobaaddons.label.announceChannel.guild", "Guild")
	}
}