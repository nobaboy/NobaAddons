package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.utils.collections.CollectionUtils.anyContains
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

object WarpPlayerHandler {
	var isWarping: Boolean = false
	var playerJoined: Boolean = false
	var player: String? = null
	private var task: Scheduler.ScheduledTask? = null

	private val inviteFailMessages = listOf("Couldn't find a player with that name!", "You cannot invite that player since they're not online.")

	fun onChatMessage(message: String) {
		when {
			inviteFailMessages.anyContains(message, ignoreCase = true) -> cancel()
			message.equals("$player is already in the party.", ignoreCase = true) -> cancel()
			message.contains("$player joined the party.", ignoreCase = true) -> playerJoined = true
		}
	}

	fun warpPlayer(playerName: String, isWarpingOut: Boolean, command: String) {
		check(!isWarping) { "Already warping another player!" }

		isWarping = true
		player = playerName

		val party = PartyAPI.party
		val membersToInvite: List<String> = party?.members?.filter { !it.isMe }?.mapNotNull { it.name } ?: listOf()

		var secondsPassed = 0
		val timeoutSeconds = if(isWarpingOut) 20 else 15
		val timeoutMessage =
			if(isWarpingOut) "Warp out failed, $player did not join the party."
			else "Warp in timed out since you did not join the party."

		if(party != null) {
			val warpType = if(isWarpingOut) "warp out" else "warp in"
			val message =
				if(party.isLeader) "Someone requested a $warpType, will re-invite everyone after $timeoutSeconds seconds."
				else "Someone requested a $warpType, re-invite me and I'll join once done."

			HypixelCommands.partyChat(message)
			if(party.isLeader) HypixelCommands.partyDisband() else HypixelCommands.partyLeave()
		}

		HypixelCommands.partyInvite(player!!)

		Scheduler.schedule(20, repeat = true) {
			if(!isWarping) return@schedule cancel()

			if(secondsPassed++ >= timeoutSeconds) {
				ChatUtils.queueCommand("$command $timeoutMessage")
				HypixelCommands.partyDisband()
				reset()
				return@schedule cancel()
			}

			if(playerJoined) {
				HypixelCommands.partyWarp()
				HypixelCommands.partyDisband()
				if(isWarpingOut) ChatUtils.queueCommand("$command Successfully warped out $player.")

				if(party != null) {
					if(party.isLeader && membersToInvite.isNotEmpty()) {
						membersToInvite.filter { it != MCUtils.playerName }.forEach(HypixelCommands::partyInvite)
					} else if(!party.isLeader) {
						party.leader.name?.let { HypixelCommands.partyJoin(it) }
					}
				}

				cancel()
			}
		}
	}

	fun cancel() {
		reset()
		task?.cancel()
	}

	fun reset() {
		isWarping = false
		playerJoined = false
		player = null
	}
}