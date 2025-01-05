package me.nobaboy.nobaaddons.features.dungeons

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.DungeonsAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.dungeons.data.SimonSaysTimes
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.lightPurple
import me.nobaboy.nobaaddons.utils.TextUtils.plus
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult

// TODO: Requires actual testing in dungeons
object SimonSaysTimer {
	private val config get() = NobaConfigManager.config.dungeons.simonSaysTimer
	private val enabled: Boolean get() = config.enabled && SkyBlockIsland.DUNGEONS.inIsland() && DungeonsAPI.inFloor(7)

	private val completionPattern by Regex("^(?<username>[A-z0-9_]+) completed a device! \\([1-7]/7\\)").fromRepo("dungeons.complete_device")
	private val buttonVec = NobaVec(110, 121, 91)

	private var startTime = Timestamp.distantPast()
	private var completionTime = Timestamp.distantPast()

	private var buttonPressed: Boolean = false
	private var deviceCompleted: Boolean = false

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
		UseBlockCallback.EVENT.register { player, _, _, hitResult -> onInteract(player, hitResult) }

		try {
			SimonSaysTimes.load()
			SimonSaysTimes.times.minOrNull()?.takeIf { !it.isNaN() }?.let { newPersonalBest ->
				if(newPersonalBest != SimonSaysTimes.personalBest) {
					SimonSaysTimes.personalBest = newPersonalBest
					SimonSaysTimes.save()
				}
			}
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to load Simon Says time data", ex)
		}
	}

	fun clearTimes() {
		val times = SimonSaysTimes.times

		if(times.isEmpty()) {
			ChatUtils.addMessage(tr("nobaaddons.command.ss.noTimes", "You have not completed a Simon Says device."))
			return
		}

		try {
			ChatUtils.addMessage(tr("nobaaddons.command.ss.cleared", "Successfully cleared Simon Says Times."))
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
			ChatUtils.addMessage(tr("nobaaddons.command.ss.noTimes", "You have not completed a Simon Says device."))
			return
		}

		val size = times.size
		val sum = times.sum()
		val average = sum / size

		val formattedAverage = "%.3f".format(average)
		ChatUtils.addMessage(tr("nobaaddons.command.ss.average", "Your average time for Simon Says is: ${formattedAverage}s (Total Count: $size)"))
	}

	fun sendPersonalBest() {
		val personalBest = SimonSaysTimes.personalBest

		if(personalBest == null) {
			ChatUtils.addMessage(tr("nobaaddons.command.ss.noTimes", "You have not completed a Simon Says device."))
			return
		}

		ChatUtils.addMessage(tr("nobaadons.command.ss.personalBest", "Your personal best Simon Says time is $personalBest"))
	}

	private fun onChatMessage(message: String) {
		if(!enabled || !buttonPressed || deviceCompleted) return

		completionPattern.onFullMatch(message) {
			val username = groups["username"]!!.value
			if(username != MCUtils.playerName) return

			completionTime = Timestamp.now()
			deviceCompleted = true

			processCompletionTime()
		}
	}

	@Suppress("SameReturnValue")
	private fun onInteract(player: PlayerEntity, hitResult: BlockHitResult): ActionResult {
		if(!enabled || buttonPressed || player != MCUtils.player || hitResult.blockPos.toNobaVec() != buttonVec) return ActionResult.PASS

		startTime = Timestamp.now()
		buttonPressed = true
		return ActionResult.PASS
	}

	private fun processCompletionTime() {
		val times: MutableList<Double> = SimonSaysTimes.times
		val timeTaken = (completionTime - startTime).inWholeMilliseconds / 1000.0
		times.add(timeTaken)

		val personalBest = SimonSaysTimes.personalBest?.takeIf { timeTaken >= it } ?: timeTaken.also { SimonSaysTimes.personalBest = it }
		val message = tr("nobaaddons.dungeons.ssTimer.completion", "Simon Says took ${timeTaken}s to complete")

		val chatMessage = if(timeTaken < personalBest) {
			tr("nobaaddons.dungeons.ssTimer.beatPb", "PERSONAL BEST!").lightPurple().bold() + " " + message
		} else {
			message + buildLiteral(" ($personalBest)") { gray() }
		}

		ChatUtils.addMessage(chatMessage)
		if(config.timeInPartyChat && PartyAPI.party != null) {
			HypixelCommands.partyChat(message.string)
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