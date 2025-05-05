package me.nobaboy.nobaaddons.features.dungeons

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.DungeonsAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.util.safeLoad
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.EventDispatcher.Companion.registerIf
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.interact.BlockInteractionEvent
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.dungeons.data.SimonSaysTimes
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.lightPurple
import me.nobaboy.nobaaddons.utils.TextUtils.plus
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.tr

object SimonSaysTimer {
	private val config get() = NobaConfig.dungeons.simonSaysTimer
	private val enabled: Boolean get() = config.enabled && SkyBlockIsland.DUNGEONS.inIsland() && DungeonsAPI.inFloor(7)

	// Change to terminal_completed and add type group
	private val DEVICE_COMPLETED_REGEX by Regex("^(?<username>[A-z0-9_]+) completed a device! \\([1-7]/7\\)").fromRepo("dungeons.device_completed")
	private val buttonLocation = NobaVec(110, 121, 91)

	private var startTime = Timestamp.distantPast()
	private var completionTime = Timestamp.distantPast()

	private var buttonPressed: Boolean = false
	private var deviceCompleted: Boolean = false

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		BlockInteractionEvent.EVENT.registerIf<BlockInteractionEvent.Interact>(this::onInteract)

		SimonSaysTimes.safeLoad().onSuccess {
			SimonSaysTimes.times.minOrNull()?.takeIf { !it.isNaN() }?.let { newPersonalBest ->
				if(newPersonalBest != SimonSaysTimes.personalBest) {
					SimonSaysTimes.personalBest = newPersonalBest
					SimonSaysTimes.save()
				}
			}
		}
	}

	fun clearTimes() {
		val times = SimonSaysTimes.times

		if(times.isEmpty()) {
			ChatUtils.addMessage(tr("nobaaddons.command.simonSaysTimer.noTimes", "You have not completed a Simon Says device."))
			return
		}

		try {
			ChatUtils.addMessage(tr("nobaaddons.command.simonSaysTimer.cleared", "Successfully cleared Simon Says Times."))
			SimonSaysTimes.personalBest = null
			SimonSaysTimes.times.clear()
			SimonSaysTimes.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save Simon Says time data", ex)
		}
	}

	fun sendAverage() {
		val times = SimonSaysTimes.times

		if(times.isEmpty()) {
			ChatUtils.addMessage(tr("nobaaddons.command.simonSaysTimer.noTimes", "You have not completed a Simon Says device."))
			return
		}

		val size = times.size
		val sum = times.sum()
		val average = sum / size

		val formattedAverage = "%.3f".format(average)
		ChatUtils.addMessage(tr("nobaaddons.command.simonSaysTimer.average", "Your average time for Simon Says is: ${formattedAverage}s (Total Count: $size)"))
	}

	fun sendPersonalBest() {
		val personalBest = SimonSaysTimes.personalBest

		if(personalBest == null) {
			ChatUtils.addMessage(tr("nobaaddons.command.simonSaysTimer.noTimes", "You have not completed a Simon Says device."))
			return
		}

		ChatUtils.addMessage(tr("nobaadons.command.simonSaysTimer.personalBest", "Your personal best Simon Says time is $personalBest"))
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled || !buttonPressed || deviceCompleted) return

		DEVICE_COMPLETED_REGEX.onFullMatch(event.cleaned) {
			val username = groups["username"]!!.value
			if(username != MCUtils.playerName) return

			completionTime = Timestamp.now()
			deviceCompleted = true

			processCompletionTime()
		}
	}

	private fun onInteract(event: BlockInteractionEvent.Interact) {
		if(!enabled || buttonPressed || event.player != MCUtils.player || event.location.roundToBlock() != buttonLocation) return

		startTime = Timestamp.now()
		buttonPressed = true
	}

	private fun processCompletionTime() {
		val times: MutableList<Double> = SimonSaysTimes.times
		val timeTaken = (completionTime - startTime).inWholeMilliseconds / 1000.0
		times.add(timeTaken)

		val personalBest = SimonSaysTimes.personalBest?.takeIf { timeTaken >= it } ?: timeTaken.also { SimonSaysTimes.personalBest = it }
		val message = tr("nobaaddons.dungeons.simonSaysTimer.completion", "Simon Says took ${timeTaken}s to complete")

		val chatMessage = if(timeTaken < personalBest) {
			tr("nobaaddons.dungeons.simonSaysTimer.personalBest", "PERSONAL BEST!").lightPurple().bold() + " " + message
		} else {
			message + buildLiteral(" ($personalBest)") { gray() }
		}

		if(config.timeInPartyChat && PartyAPI.party != null) {
			HypixelCommands.partyChat(message.string)
		} else {
			ChatUtils.addMessage(chatMessage)
		}

		try {
			SimonSaysTimes.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save Simon Says time data", ex)
		}
	}

	private fun reset() {
		buttonPressed = false
		deviceCompleted = false
	}
}