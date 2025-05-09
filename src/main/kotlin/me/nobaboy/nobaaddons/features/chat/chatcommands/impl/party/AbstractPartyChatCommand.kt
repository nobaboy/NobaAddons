package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class AbstractPartyChatCommand(defaultCooldown: Duration = 1.seconds) : ChatCommand(defaultCooldown) {
	open val requireClientPlayerIs: ClientboundPartyInfoPacket.PartyRole = ClientboundPartyInfoPacket.PartyRole.MEMBER
	open val requireExecutorIs: ClientboundPartyInfoPacket.PartyRole = ClientboundPartyInfoPacket.PartyRole.MEMBER
}