package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.commands.InstanceCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import kotlin.time.Duration.Companion.seconds

class JoinInstanceCommands : AbstractPartyChatCommand(2.seconds) {
	override val enabled: Boolean = config.party.joinInstanced
	override val requireClientPlayerIs = ClientboundPartyInfoPacket.PartyRole.LEADER

	override val name: String = "f1"
	override val aliases: List<String> = listOf(
		/*f1,*/ "f2", "f3", "f4", "f5", "f6", "f7", // Catacombs
		"m1", "m2", "m3", "m4", "m5", "m6", "m7", // Master Mode Catacombs
		"t1", "t2", "t3", "t4", "t5",             // Kuudra
	)
	override val usage: String = "f(1-7), m(1-7), t(1-5)"

	override suspend fun run(ctx: ChatContext) {
		InstanceCommands.joinInstance(ctx.command)
		startCooldown()
	}
}