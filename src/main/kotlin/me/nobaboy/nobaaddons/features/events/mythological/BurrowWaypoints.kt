package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.mythological.BurrowAPI
import me.nobaboy.nobaaddons.api.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockAt
import me.nobaboy.nobaaddons.utils.BlockUtils.inLoadedChunk
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.time.Duration.Companion.seconds

object BurrowWaypoints {
	private val config get() = NobaConfigManager.config.events.mythological

	private val playerLocation get() = LocationUtils.playerLocation()

	private val validBlocks = listOf(
		Blocks.AIR,
		Blocks.OAK_LEAVES,
		Blocks.SPRUCE_LEAVES,
		Blocks.SPRUCE_FENCE,
		Blocks.SHORT_GRASS,
		Blocks.TALL_GRASS,
		Blocks.FERN,
		Blocks.POPPY,
		Blocks.DANDELION,
		Blocks.OXEYE_DAISY,
		Blocks.BLUE_ORCHID,
		Blocks.AZURE_BLUET,
		Blocks.ROSE_BUSH,
		Blocks.LILAC
	)

	private val burrows = mutableMapOf<NobaVec, BurrowType>()
	private var guessLocation: NobaVec? = null

	private var nearestWarp: BurrowWarpLocations.WarpPoint? = null

	private var shouldFocusInquisitor = false

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { reset() }
		MythologicalEvents.BURROW_GUESS.register(this::onBurrowGuess)
		MythologicalEvents.BURROW_FIND.register(this::onBurrowFind)
		MythologicalEvents.BURROW_DIG.register(this::onBurrowDig)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onBurrowGuess(event: MythologicalEvents.BurrowGuess) {
		guessLocation = event.location
	}

	private fun onBurrowFind(event: MythologicalEvents.BurrowFind) {
		val location = event.location
		burrows[location] = event.type

		guessLocation?.let { guess ->
			val adjustedGuess = findValidLocation(guess)
			if(adjustedGuess.distance(location) < 10) guessLocation = null
		}
	}

	private fun onBurrowDig(event: MythologicalEvents.BurrowDig) {
		burrows.remove(event.location)
	}

	private fun onChatMessage(message: String) {
		if(!isEnabled()) return

		when {
			message.startsWith(" ☠ You were killed by") -> burrows.remove(BurrowAPI.mobBurrow)
			message == "Poof! You have cleared your griffin burrows!" -> reset()
			message == "You haven't unlocked this fast travel destination!" -> {
				nearestWarp?.let {
					ChatUtils.addMessage("It appears as you don't have the ${it.displayName} warp location unlocked.")
					ChatUtils.addMessage("Once you have unlocked that location, use '/noba mythological resetwarps'.")
					it.unlocked = false
					nearestWarp = null
				}
			}
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!isEnabled()) return

		suggestNearestWarp()

		shouldFocusInquisitor = renderInquisitorWaypoints(context)
		if(shouldFocusInquisitor) return

		if(config.findNearbyBurrows) renderBurrowWaypoints(context)
		if(config.burrowGuess) renderGuessLocation(context)
	}

	private fun renderInquisitorWaypoints(context: WorldRenderContext): Boolean {
		if(InquisitorWaypoints.waypoints.isEmpty()) return false

		InquisitorWaypoints.waypoints.forEach { inquisitor ->
			val location = inquisitor.location
			val distance = location.distanceSq(playerLocation)

			RenderUtils.renderWaypoint(context, location, NobaColor.DARK_RED, throughBlocks = true)
			RenderUtils.renderText(context, location.raise(), "Inquisitor", NobaColor.DARK_RED, yOffset = -20.0f)

			if(config.inquisitorDespawnTime) {
				val spawnTime = inquisitor.spawnTime
				val formattedTime = (75 - spawnTime.elapsedSince().inWholeSeconds).toInt()

				RenderUtils.renderText(context, location.raise(), "Despawns in ${formattedTime}s")
			}

			if(distance < 5) InquisitorWaypoints.tryRemove(inquisitor)

			if(config.inquisitorFocusMode) return true
		}

		return false
	}

	private fun renderBurrowWaypoints(context: WorldRenderContext) {
		burrows.forEach { location, type ->
			RenderUtils.renderWaypoint(context, location, type.color, throughBlocks = true)
			RenderUtils.renderText(context, location.center().raise(), type.text, type.color, yOffset = -5.0f)
		}
	}

	private fun renderGuessLocation(context: WorldRenderContext) {
		guessLocation?.let {
			val adjustedLocation = findValidLocation(it)
			val distance = adjustedLocation.distance(playerLocation)

			RenderUtils.renderWaypoint(context, adjustedLocation, NobaColor.YELLOW, throughBlocks = distance > 10)
			RenderUtils.renderText(context, adjustedLocation.center().raise(), "Guess", NobaColor.YELLOW, yOffset = -10.0f)

			if(distance > 5) {
				val formattedDistance = distance.toInt().addSeparators()
				RenderUtils.renderText(context, adjustedLocation.center().raise(), "${formattedDistance}m", NobaColor.YELLOW)
			}
		}
	}

	private fun reset() {
		guessLocation = null
		burrows.clear()
	}

	private fun suggestNearestWarp() {
		if(!config.findNearestWarp) return

		val targetLocation = getTargetLocation() ?: return
		nearestWarp = BurrowWarpLocations.getNearestWarp(targetLocation) ?: return

		val playerToTargetDistance = playerLocation.distance(targetLocation)
		val warpToTargetDistance = nearestWarp!!.location.distance(targetLocation)
		if(playerToTargetDistance - warpToTargetDistance < 40) return

		RenderUtils.drawTitle(Text.literal("Warp to ${nearestWarp!!.displayName}").formatted(Formatting.GRAY), 1.seconds)
	}

	private fun getTargetLocation(): NobaVec? {
		InquisitorWaypoints.waypoints.firstOrNull()?.let { return it.location }
		return guessLocation?.let { findValidLocation(it) }
	}

	private fun findValidLocation(location: NobaVec): NobaVec {
		if(!location.inLoadedChunk()) return location.copy(y = playerLocation.y)

		return findGroundLevel(location) ?: findFirstSolidBelowAir(location)
	}

	private fun findGroundLevel(location: NobaVec): NobaVec? {
		for(y in 140 downTo 65) {
			if(location.isGroundAt(y)) return location.copy(y = y.toDouble())
		}
		return null
	}

	private fun findFirstSolidBelowAir(location: NobaVec): NobaVec {
		for(y in 65..140) {
			if(location.copy(y = y.toDouble()).getBlockAt() == Blocks.AIR) {
				return location.copy(y = (y - 1).toDouble())
			}
		}
		return location.copy(y = playerLocation.y)
	}

	fun useNearestWarp() {
		nearestWarp?.let {
			val command = "warp ${it.warpName}"
			ChatUtils.queueCommand(command)
			nearestWarp = null
		}
	}

	private fun NobaVec.isGroundAt(y: Int) = copy(y = y.toDouble()).getBlockAt() == Blocks.GRASS_BLOCK &&
		copy(y = y + 1.0).getBlockAt() in validBlocks

	private fun isEnabled() = DianaAPI.isActive()
}