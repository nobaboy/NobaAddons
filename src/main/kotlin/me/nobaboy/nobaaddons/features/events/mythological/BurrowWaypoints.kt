package me.nobaboy.nobaaddons.features.events.mythological

import kotlinx.datetime.Instant
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.BurrowAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.mc.LocationUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.mc.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.mc.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TimeUtils.elapsedSince
import me.nobaboy.nobaaddons.utils.TimeUtils.now
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import kotlin.time.Duration.Companion.seconds

object BurrowWaypoints {
	private val config get() = NobaConfig.events.mythological
	private val enabled: Boolean get() = DianaAPI.isActive

	private val burrows = mutableMapOf<NobaVec, BurrowType>()
	private var guessLocation: NobaVec? = null

	private var nearestWarp: NearestWarp? = null
	private var lastWarpSuggestTime = Instant.DISTANT_PAST

	private val isInquisitorSpawned: Boolean
		get() = InquisitorWaypoints.inquisitors.isNotEmpty()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		MythologicalEvents.BURROW_GUESS.register(this::onBurrowGuess)
		MythologicalEvents.BURROW_FIND.register(this::onBurrowFind)
		MythologicalEvents.BURROW_DIG.register(this::onBurrowDig)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
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
		if(config.removeGuessOnBurrowFind && guessLocation?.let { it.distance(location) < 3 } == true) {
			guessLocation = null
		}
	}

	private fun onBurrowDig(event: MythologicalEvents.BurrowDig) {
		burrows.remove(event.location)
		if(config.removeGuessOnBurrowFind) guessLocation = null
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return

		val message = event.cleaned

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
		if(!isInquisitorSpawned) return

		InquisitorWaypoints.inquisitors.toList().forEach { inquisitor ->
			val location = inquisitor.location
			val adjustedLocation = location.center().raise()
			val yOffset = if(config.showInquisitorDespawnTime) -20f else -10f

			val distance = location.distance(LocationUtils.playerLocation, center = true)

			RenderUtils.renderWaypoint(context, location, NobaColor.DARK_RED, throughBlocks = true)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				tr("nobaaddons.events.mythological.inquisitor", "Inquisitor"),
				color = NobaColor.DARK_RED,
				yOffset = yOffset,
				hideThreshold = 5.0,
				throughBlocks = true,
			)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				inquisitor.spawner,
				color = NobaColor.GOLD,
				yOffset = yOffset + 10f,
				hideThreshold = 5.0,
				throughBlocks = true
			)

			if(config.showInquisitorDespawnTime) {
				RenderUtils.renderText(
					context,
					adjustedLocation,
					tr("nobaaddons.events.mythological.inquisitorDespawnsIn", "Despawns in ${inquisitor.remainingTime}"),
					color = NobaColor.GRAY,
					hideThreshold = 5.0,
					throughBlocks = true,
				)
			}

			if(distance < 5) InquisitorWaypoints.tryRemove(inquisitor)
		}
	}

	private fun renderBurrowWaypoints(context: WorldRenderContext) {
		burrows.forEach { (location, type) ->
			val text = if(location == guessLocation) {
				type.displayName.copy().append(" (Guess)".toText().aqua())
			} else {
				type.displayName
			}

			RenderUtils.renderWaypoint(context, location, type.color, throughBlocks = true)
			RenderUtils.renderText(
				context,
				location.center().raise(),
				text,
				color = type.color,
				yOffset = -5f,
				hideThreshold = 5.0,
				throughBlocks = true
			)
		}
	}

	private fun renderGuessLocation(context: WorldRenderContext) {
		if(guessLocation in burrows) return

		guessLocation?.let {
			val adjustedLocation = it.center().raise()
			val distance = it.distance(LocationUtils.playerLocation, center = true)
			val formattedDistance = distance.toInt().addSeparators()

			RenderUtils.renderWaypoint(context, it, NobaColor.AQUA, throughBlocks = distance > 10)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				tr("nobaaddons.events.mythological.burrowGuessWaypoint", "Burrow Guess"),
				color = NobaColor.AQUA,
				yOffset = -10f,
				hideThreshold = 5.0,
				throughBlocks = true,
			)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				"${formattedDistance}m",
				color = NobaColor.GRAY,
				hideThreshold = 5.0,
				throughBlocks = true
			)
		}
	}

	private fun suggestNearestWarp() {
		if(!config.findNearestWarp) return
		if(lastWarpSuggestTime.elapsedSince() < 1.seconds) return

		val targetLocation = getTargetLocation() ?: return
		nearestWarp = BurrowWarpLocations.getNearestWarp(targetLocation) ?: return
		lastWarpSuggestTime = Instant.now

		RenderUtils.drawTitle(tr("nobaaddons.events.mythological.warpToPoint", "Warp to ${nearestWarp!!.warpPoint}"), NobaColor.GRAY, 2f, 30, 1.seconds)
	}

	private fun getTargetLocation(): NobaVec? = InquisitorWaypoints.inquisitors.firstOrNull()?.location ?: guessLocation

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
		burrows.clear()
		guessLocation = null
	}
}