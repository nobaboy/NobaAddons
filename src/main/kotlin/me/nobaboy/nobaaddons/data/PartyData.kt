package me.nobaboy.nobaaddons.data

import com.mojang.authlib.yggdrasil.ProfileResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.future.asCompletableFuture
import me.nobaboy.nobaaddons.utils.MCUtils
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import java.util.UUID

data class PartyData(val leaderUUID: UUID, val members: List<Member>) {
	val isLeader: Boolean
		get() = MCUtils.player!!.uuid == leaderUUID

	val leader: Member by lazy {
		members.first { it.isLeader }
	}

	data class Member(val uuid: UUID, val profile: Deferred<ProfileResult?>, val role: ClientboundPartyInfoPacket.PartyRole) {
		// we're using .asCompletableFuture() here as .getCompleted() is still experimental
		private val profileFuture by lazy { profile.asCompletableFuture() }

		val name: String? get() = runCatching { profileFuture.getNow(null)?.profile?.name }.getOrNull()

		val isMe: Boolean get() = MCUtils.player!!.uuid == uuid
		val isLeader: Boolean get() = role == ClientboundPartyInfoPacket.PartyRole.LEADER
		val isMod: Boolean get() = role == ClientboundPartyInfoPacket.PartyRole.MOD
	}
}