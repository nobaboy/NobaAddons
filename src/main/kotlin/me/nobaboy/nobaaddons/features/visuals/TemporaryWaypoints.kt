package me.nobaboy.nobaaddons.features.visuals

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.RegexUtils.onPartialMatch
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object TemporaryWaypoints {
	private val config get() = NobaConfig.uiAndVisuals.temporaryWaypoints
	val enabled: Boolean get() = config.enabled

	private val waypoints = mutableListOf<Waypoint>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { waypoints.clear() }
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return
		if(!SkyBlockAPI.inSkyBlock) return
		if(DianaAPI.isActive) return

		CommonPatterns.CHAT_COORDINATES_REGEX.onPartialMatch(event.cleaned) {
			val username = groups["username"]!!.value
			if(username == MCUtils.playerName) return

			val x = groups["x"]!!.value.toDouble()
			val y = groups["y"]!!.value.toDouble()
			val z = groups["z"]!!.value.toDouble()
			val location = NobaVec(x, y, z).roundToBlock()

			val info = groups["info"]?.value?.take(32) ?: ""

			val text = "$username$info"
			waypoints.add(Waypoint(location, text, Timestamp.now(), config.expirationTime.seconds))
			ChatUtils.addMessage(tr("nobaaddons.uiAndVisuals.temporaryWaypoints.fromChat", "Temporary Waypoint added at x: $x, y: $y, z: $z from $username"))
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		val cameraPos = context.camera().pos.toNobaVec()
		val color = config.waypointColor

		waypoints.removeIf { it.expired || it.location.distance(cameraPos) < 5.0 }
		waypoints.forEach { waypoint ->
			val location = waypoint.location
			val adjustedLocation = location.center()

			val distance = location.distanceToPlayer()
			val formattedDistance = distance.toInt().addSeparators()

			RenderUtils.renderWaypoint(context, location, color, throughBlocks = true)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				waypoint.text,
				color = color,
				yOffset = -10f,
				hideThreshold = 5.0,
				throughBlocks = true
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

	fun addWaypoint(x: Double, y: Double, z: Double, name: String) {
		waypoints.add(Waypoint(NobaVec(x, y, z), name, Timestamp.now(), null))
	}

	data class Waypoint(val location: NobaVec, val text: String, val timestamp: Timestamp, val duration: Duration?) {
		val expired: Boolean
			get() = duration != null && timestamp.elapsedSince() >= duration
	}
}