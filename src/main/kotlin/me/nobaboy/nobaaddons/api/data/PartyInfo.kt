package me.nobaboy.nobaaddons.api.data

import me.nobaboy.nobaaddons.utils.MCUtils
import java.util.UUID

data class PartyInfo(var leader: UUID, var members: Map<UUID, String>) {
	val isLeader: Boolean
		get() = MCUtils.player?.uuid == leader

	val leaderName: String
		get() = members[leader]!!
}