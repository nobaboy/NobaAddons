package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.events.mythological.BurrowAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockAt
import me.nobaboy.nobaaddons.utils.BlockUtils.inLoadedChunk
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import kotlin.time.Duration.Companion.seconds

object BurrowWaypoints {
	private val config get() = NobaConfig.INSTANCE.events.mythological
	private val enabled: Boolean get() = DianaAPI.isActive

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

	private var nearestWarp: NearestWarp? = null
	private var lastWarpSuggestTime = Timestamp.distantPast()

	private val isInquisitorSpawned: Boolean
		get() = InquisitorWaypoints.waypoints.isNotEmpty()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		MythologicalEvents.BURROW_GUESS.register(this::onBurrowGuess)
		MythologicalEvents.BURROW_FIND.register(this::onBurrowFind)
		MythologicalEvents.BURROW_DIG.register(this::onBurrowDig)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onBurrowGuess(event: MythologicalEvents.BurrowGuess) {
		guessLocation = event.location
	}

	private fun onBurrowFind(event: MythologicalEvents.BurrowFind) {
		val location = event.location
		if(location in burrows) return

		burrows[location] = event.type

		if(config.dingOnBurrowFind) SoundUtils.plingSound.play()
		if(!config.removeGuessOnBurrowFind) return

		guessLocation?.let { guess ->
			val adjustedGuess = findValidLocation(guess)
			if(adjustedGuess.distance(location) < 10) guessLocation = null
		}
	}

	private fun onBurrowDig(event: MythologicalEvents.BurrowDig) {
		burrows.remove(event.location)
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		when {
			message.startsWith(" ☠ You were killed by") -> burrows.remove(BurrowAPI.mobBurrow)
			message == "Poof! You have cleared your griffin burrows!" -> reset()
			message == "You haven't unlocked this fast travel destination!" -> {
				nearestWarp?.let {
					ChatUtils.addMessage(tr("nobaaddons.events.mythological.warpNotUnlocked", "It appears as you don't have the ${it.warpPoint} warp location unlocked."))
					ChatUtils.addMessage(tr("nobaaddons.events.mythological.resetCommandHint", "Once you have unlocked that location, use '/noba mythological resetwarps'."))
					it.warpPoint.unlocked = false
					nearestWarp = null
				}
			}
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		suggestNearestWarp()
		renderInquisitorWaypoints(context)

		if(isInquisitorSpawned && config.inquisitorFocusMode) return

		if(config.findNearbyBurrows) renderBurrowWaypoints(context)
		if(config.burrowGuess) renderGuessLocation(context)
	}

	private fun renderInquisitorWaypoints(context: WorldRenderContext) {
		if(InquisitorWaypoints.waypoints.isEmpty()) return

		InquisitorWaypoints.waypoints.toList().forEach { inquisitor ->
			val location = inquisitor.location
			val distance = location.distance(playerLocation)
			val yOffset = if(config.showInquisitorDespawnTime) -20.0f else -10.0f

			RenderUtils.renderWaypoint(context, location, NobaColor.DARK_RED, throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), "Inquisitor", NobaColor.DARK_RED, yOffset = yOffset, hideThreshold = 5.0, throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), inquisitor.spawner, NobaColor.GOLD, yOffset = yOffset + 10.0f, hideThreshold = 5.0, throughBlocks = true)

			if(config.showInquisitorDespawnTime) {
				val spawnTime = inquisitor.spawnTime
				val formattedTime = (75 - spawnTime.elapsedSince().inWholeSeconds).toInt()

				RenderUtils.renderText(location.center().raise(), "Despawns in ${formattedTime}s", NobaColor.GRAY, hideThreshold = 5.0, throughBlocks = true)
			}

			if(distance < 5) InquisitorWaypoints.tryRemove(inquisitor)
		}
	}

	private fun renderBurrowWaypoints(context: WorldRenderContext) {
		burrows.forEach { location, type ->
			RenderUtils.renderWaypoint(context, location, type.color, throughBlocks = true)
			RenderUtils.renderText(location.center().raise(), type.displayName, type.color, yOffset = -5.0f, hideThreshold = 5.0, throughBlocks = true)
		}
	}

	private fun renderGuessLocation(context: WorldRenderContext) {
		guessLocation?.let {
			val adjustedLocation = findValidLocation(it)
			val distance = adjustedLocation.distance(playerLocation)

			RenderUtils.renderWaypoint(context, adjustedLocation, NobaColor.AQUA, throughBlocks = distance > 10)
			RenderUtils.renderText(adjustedLocation.center().raise(), "Burrow Guess", NobaColor.AQUA, yOffset = -10.0f, hideThreshold = 5.0, throughBlocks = true)

			if(distance > 5) {
				val formattedDistance = distance.toInt().addSeparators()
				RenderUtils.renderText(adjustedLocation.center().raise(), "${formattedDistance}m", NobaColor.GRAY, hideThreshold = 5.0, throughBlocks = true)
			}
		}
	}

	private fun suggestNearestWarp() {
		if(!config.findNearestWarp) return
		if(lastWarpSuggestTime.elapsedSince() < 1.seconds) return

		val targetLocation = getTargetLocation() ?: return
		nearestWarp = BurrowWarpLocations.getNearestWarp(targetLocation) ?: return

		lastWarpSuggestTime = Timestamp.now()
		RenderUtils.drawTitle(tr("nobaaddons.events.mythological.warpToPoint", "Warp to ${nearestWarp!!.warpPoint}"), NobaColor.GRAY, 2.0f, 30, 1.seconds)
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

	private fun NobaVec.isGroundAt(y: Int) = copy(y = y.toDouble()).getBlockAt() == Blocks.GRASS_BLOCK &&
		copy(y = y + 1.0).getBlockAt() in validBlocks

	fun useNearestWarp() {
		if(!enabled) return

		nearestWarp?.let {
			if(it.used) return

			val command = "warp ${it.warpPoint.warpName}"
			ChatUtils.queueCommand(command)
			it.used = true
		}
	}

	fun reset() {
		guessLocation = null
		burrows.clear()
	}
}