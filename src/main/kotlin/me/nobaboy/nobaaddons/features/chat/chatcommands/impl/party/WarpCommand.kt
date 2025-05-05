package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import org.apache.commons.lang3.StringUtils
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class WarpCommand : AbstractPartyChatCommand(3.seconds) {
	companion object {
		private var job: Job? = null

		fun cancel() {
			job?.cancel()
		}
	}

	override val requireClientPlayerIs = ClientboundPartyInfoPacket.PartyRole.LEADER
	override val enabled: Boolean get() = config.party.warp
	override val name: String = "warp"
	override val usage: String = "warp [optional: delay]"

	override suspend fun run(ctx: ChatContext) {
		warpParty(ctx.args.getOrNull(0))
		startCooldown()
	}

	private fun warpParty(seconds: String?) {
		if(seconds == null) {
			cancel()
			HypixelCommands.partyWarp()
			return
		}

		if(job?.isCompleted == false) {
			return
		}

		if(!StringUtils.isNumeric(seconds) || seconds.toInt() > 15 || seconds.toInt() < 3) {
			HypixelCommands.partyChat("Delay can only range from 3 to 15 seconds")
			return
		}

		val delay = seconds.toInt()
		HypixelCommands.partyChat("Warping in $delay (To cancel type '!cancel')")
		job = NobaAddons.runAsync { timedWarp(delay.seconds) }
	}

	private suspend fun timedWarp(delay: Duration) {
		var timeLeft = delay - 1.seconds

		while(timeLeft > 0.seconds) {
			HypixelCommands.partyChat(timeLeft.inWholeSeconds.toString())
			timeLeft -= 1.seconds
			delay(1.seconds)
		}

		HypixelCommands.partyWarp()
	}
}