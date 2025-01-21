package me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import org.apache.commons.lang3.StringUtils
import kotlin.time.Duration.Companion.seconds

class WarpCommand : ChatCommand(3.seconds) {
	companion object {
		var cancel = false
	}

	private var delay = 0
	private var isWarping = false

	override val enabled: Boolean get() = config.party.warp

	override val name: String = "warp"

	override val usage: String = "warp [optional: delay]"

	override fun run(ctx: ChatContext) {
		if(PartyAPI.party?.isLeader != true) return

		val time = if(ctx.args.isEmpty()) null else ctx.args[0]
		warpParty(time)
		startCooldown()
	}

	private fun warpParty(seconds: String?) {
		if(seconds == null) {
			HypixelCommands.partyWarp()
		} else if(!StringUtils.isNumeric(seconds) || seconds.toInt() > 15 || seconds.toInt() < 3) {
			HypixelCommands.partyChat("Delay can only range from 3 to 15 seconds")
		} else {
			delay = seconds.toInt()
			isWarping = true
			startTimedWarp()
		}
	}

	private fun startTimedWarp() {
		HypixelCommands.partyChat("Warping in $delay (To cancel type '!cancel')")
		Scheduler.schedule(20, repeat = true) {
			if(cancel) {
				HypixelCommands.partyChat("Warp cancelled...")
				isWarping = false
				cancel = false
				return@schedule cancel()
			}

			if(--delay > 0) {
				HypixelCommands.partyChat(delay.toString())
			} else {
				HypixelCommands.partyWarp()
				isWarping = false
				return@schedule cancel()
			}
		}
	}
}