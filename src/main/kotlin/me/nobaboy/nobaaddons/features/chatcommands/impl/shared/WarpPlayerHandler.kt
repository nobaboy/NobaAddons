package me.nobaboy.nobaaddons.features.chatcommands.impl.shared

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.utils.Utils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import kotlin.time.Duration.Companion.seconds

object WarpPlayerHandler {
	var isWarping: Boolean = false
	var playerJoined: Boolean = false
	var player: String? = null
	private var task: Job? = null

	fun warpPlayer(playerName: String, isWarpingOut: Boolean, command: String) {
		isWarping = true
		player = playerName

		val partyStatus = PartyAPI
		val membersToInvite: MutableList<String> =
			if(partyStatus.isLeader()) partyStatus.partyMembers.toMutableList()
			else mutableListOf()

		task = NobaAddons.runAsync {
			var secondsPassed = 0
			val timeoutSeconds = if(isWarpingOut) 20 else 15
			val timeoutMessage =
				if(isWarpingOut) "Warp out failed, $player did not join the party." else
					"Warp in timed out since you did not join the party."

			if(partyStatus.inParty) {
				val chatMessage = if(isWarpingOut) "warp out" else "warp in"
				val message =
					if(partyStatus.isLeader()) "Someone requested a $chatMessage, will re-invite everyone after $timeoutSeconds seconds." else
						"Someone requested a $chatMessage, re-invite me and I'll join once done."

				HypixelCommands.partyChat(message)
				if(partyStatus.isLeader()) HypixelCommands.partyDisband() else
					HypixelCommands.partyLeave()
			}

			HypixelCommands.partyInvite(player!!)

			while(isWarping) {
				if(secondsPassed++ >= timeoutSeconds) {
					ChatUtils.queueCommand("$command $timeoutMessage")
					HypixelCommands.partyDisband()
					reset(false)
					break
				}

				if(playerJoined) {
					HypixelCommands.partyWarp()
					HypixelCommands.partyDisband()
					if(isWarpingOut) {
						HypixelCommands.guildChat("Successfully warped out $player.")
					}
					reset(false)
					break
				}

				delay(1.seconds)
			}

			if(partyStatus.isLeader() && membersToInvite.isNotEmpty()) {
				membersToInvite.forEach {
					if(it != Utils.getPlayerName()) HypixelCommands.partyInvite(it)
				}
			} else if(!partyStatus.isLeader() && partyStatus.inParty) {
				HypixelCommands.partyJoin(partyStatus.partyLeader!!)
			}
		}
	}

	fun reset(cancel: Boolean) {
		if(cancel) task?.cancel()
		isWarping = false
		playerJoined = false
		player = null
	}
}