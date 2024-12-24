package me.nobaboy.nobaaddons.data

import me.nobaboy.nobaaddons.data.json.MojangProfile
import me.nobaboy.nobaaddons.utils.MCUtils
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import java.util.UUID

data class PartyData(val leaderUUID: UUID, val members: List<Member>) {
	val isLeader: Boolean
		get() = MCUtils.player!!.uuid == leaderUUID

	val leader: Member by lazy {
		members.first { it.isLeader }
	}

	data class Member(val uuid: UUID, val profile: MojangProfile, val role: ClientboundPartyInfoPacket.PartyRole) {
		val name: String get() = profile.name
		val isMe: Boolean get() = MCUtils.player!!.uuid == uuid
		val isLeader: Boolean get() = role == ClientboundPartyInfoPacket.PartyRole.LEADER
		val isMod: Boolean get() = role == ClientboundPartyInfoPacket.PartyRole.MOD
	}
}