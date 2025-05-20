package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.data.PartyData
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.collections.CollectionUtils.anyContains
import me.nobaboy.nobaaddons.utils.hypixel.HypixelCommands
import me.nobaboy.nobaaddons.utils.mc.chat.ChatUtils
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object WarpPlayerHandler {
	private val INVITATION_FAIL_MESSAGES by Repo.list(
		"Couldn't find a player with that name!".fromRepo("party.invite_other.not_found"),
		"You cannot invite that player since they're not online.".fromRepo("party.invite_other.offline"),
		"You cannot invite that player since they have blocked you.".fromRepo("party.invite_other.blocked"),
		"You cannot invite that player.".fromRepo("party.invite_other.generic_unallowed"),
	)

	val isWarping: Boolean get() = targetPlayer != null

	private var targetPlayer: String? = null
	private var state = State.INACTIVE
	private var task: Job? = null

	init {
		ChatMessageEvents.CHAT.register { onChatMessage(it.cleaned) }
	}

	private fun onChatMessage(message: String) {
		if(targetPlayer == null) return

		when {
			INVITATION_FAIL_MESSAGES.anyContains(message, ignoreCase = true) -> state = State.CANT_INVITE
			message.equals("$targetPlayer is already in the party.", ignoreCase = true) -> cancel()
			message.contains("$targetPlayer joined the party.", ignoreCase = true) -> state = State.JOINED
		}
	}

	fun warpPlayer(playerName: String, isWarpingOut: Boolean, command: String) {
		check(!isWarpingOut) { "Already warping another player!" }
		targetPlayer = playerName
		state = State.INACTIVE
		task = NobaAddons.runAsync { warpInternal(playerName, isWarpingOut, command) }
	}

	fun cancel() {
		task?.cancel()
		reset()
	}

	private suspend fun warpInternal(playerName: String, isWarpingOut: Boolean, command: String) {
		val party = PartyAPI.party // get a copy of the party before we leave
		val timeout = if(isWarpingOut) 20.seconds else 15.seconds
		var elapsed = 0.seconds

		if(party != null) leaveParty(party, isWarpingOut, timeout)

		// wait until the invite is sent to actually start the timer, as its possible there may be several commands in
		// the queue from the prior party leave
		ChatUtils.queueCommandAndWait("party invite $playerName")
		state = State.INVITED
		while(elapsed < timeout) {
			when(state) {
				State.CANT_INVITE -> break
				State.JOINED -> {
					HypixelCommands.partyWarp()
					break
				}
				else -> { /* noop */ }
			}

			elapsed += 1.seconds
			delay(1.seconds)
		}

		if(elapsed >= timeout) {
			val timeoutMessage = when {
				isWarpingOut -> "Warp out failed, $playerName did not join the party."
				else -> "Warp in timed out since you did not join the party."
			}
			ChatUtils.queueCommand("$command $timeoutMessage")
		} else if(isWarpingOut) {
			when(state) {
				State.CANT_INVITE -> ChatUtils.queueCommand("$command Couldn't invite $playerName.")
				else -> ChatUtils.queueCommand("$command Successfully warped out $playerName.")
			}
		}

		HypixelCommands.partyDisband()
		party?.let(::reparty)
		reset()
	}

	private fun leaveParty(party: PartyData, isWarpingOut: Boolean, timeout: Duration) {
		val warpType = if(isWarpingOut) "warp out" else "warp in"
		val message = when {
			party.isLeader -> "Someone requested a $warpType, will re-invite everyone within ${timeout.inWholeSeconds} seconds."
			else -> "Someone requested a $warpType, re-invite me and I'll join once done."
		}

		HypixelCommands.partyChat(message)
		if(party.isLeader) HypixelCommands.partyDisband() else HypixelCommands.partyLeave()
	}

	private fun reparty(party: PartyData) {
		if(!party.isLeader) {
			HypixelCommands.partyJoin(party.leader.name ?: return)
			return
		}

		party.members
			.asSequence()
			.filterNot(PartyData.Member::isMe)
			.mapNotNull(PartyData.Member::name)
			.forEach(HypixelCommands::partyInvite)
	}

	private fun reset() {
		state = State.INACTIVE
		targetPlayer = null
	}

	private enum class State {
		INACTIVE,
		INVITED,
		CANT_INVITE,
		JOINED,
		;
	}
}