package me.nobaboy.nobaaddons.features.dungeons

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.DungeonsAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.dungeons.data.SimonSaysTimes
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.hit.BlockHitResult
import java.util.regex.Pattern

// TODO: Requires actual testing in dungeons
object SimonSaysTimer {
	private val config get() = NobaConfigManager.config.dungeons.simonSaysTimer
	private val enabled: Boolean get() = SkyBlockIsland.DUNGEONS.inIsland() && DungeonsAPI.inFloor(7) && config.enabled

	private val completionPattern = Pattern.compile("^(?<username>[A-z0-9_]+) completed a device! \\([1-7]/7\\)")
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
			NobaAddons.LOGGER.error("Failed to load simon-says-timer.json", ex)
		}
	}

	fun clearTimes() {
		val times = SimonSaysTimes.times

		if(times.isEmpty()) {
			ChatUtils.addMessage("You have not completed a Simon Says device.")
			return
		}

		try {
			ChatUtils.addMessage("Successfully cleared Simon Says Times.")
			SimonSaysTimes.personalBest = null
			SimonSaysTimes.times.clear()
			SimonSaysTimes.save()
		} catch(ex: IOException) {
			NobaAddons.LOGGER.error("Failed to modify simon-says-timer.json", ex)
		}
	}

	fun sendAverage() {
		val times = SimonSaysTimes.times

		if(times.isEmpty()) {
			ChatUtils.addMessage("You have not completed a Simon Says device.")
			return
		}

		val size = times.size
		val sum = times.sum()
		val average = sum / size

		val formattedAverage = "%.3f".format(average)
		ChatUtils.addMessage("Your average time for Simon Says is: ${formattedAverage}s (Total Count: $size)")
	}

	fun sendPersonalBest() {
		val personalBest = SimonSaysTimes.personalBest

		val message = personalBest?.let {
			"Your Simon Says Personal Best is: $personalBest"
		} ?: "You have not completed a Simon Says device."
		ChatUtils.addMessage(message)
	}

	private fun onChatMessage(message: String) {
		if(!enabled || !buttonPressed || deviceCompleted) return

		completionPattern.matchMatcher(message) {
			val username = group("username")
			if(username != MCUtils.playerName) return

			completionTime = Timestamp.now()
			deviceCompleted = true

			processCompletionTime()
		}
	}

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
		val classifier = if(timeTaken < personalBest) Text.literal("(PB)").formatted(Formatting.DARK_AQUA, Formatting.BOLD)
			else Text.literal("($personalBest)").formatted(Formatting.DARK_AQUA)
		val message = buildText {
			formatted(Formatting.AQUA)
			append("Took ")
			append(timeTaken.toString())
			append("s to finish the Simon Says Device. ")
			append(classifier)
		}

		if(config.timeInPartyChat && PartyAPI.party != null) HypixelCommands.partyChat(message.string) else ChatUtils.addMessage(message)

		try {
			SimonSaysTimes.save()
		} catch(ex: IOException) {
			NobaAddons.LOGGER.error("Failed to save simon-says-timer.json", ex)
		}
	}

	private fun reset() {
		buttonPressed = false
		deviceCompleted = false
	}
}