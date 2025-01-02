package me.nobaboy.nobaaddons.features.visuals

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.RegexUtils.onPartialMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object TemporaryWaypoints {
	private val config get() = NobaConfigManager.config.uiAndVisuals.temporaryWaypoints
	val enabled: Boolean get() = config.enabled

	private val chatCoordsPattern by Regex(
		"(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+)(?<info>.*)"
	).fromRepo("temporary_waypoints.coordinates")

	private val waypoints = mutableListOf<Waypoint>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { waypoints.clear() }
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return
		if(!SkyBlockAPI.inSkyBlock) return
		if(DianaAPI.isActive) return

		chatCoordsPattern.onPartialMatch(message) {
			val username = groups["username"]!!.value
			if(username == MCUtils.playerName) return

			val x = groups["x"]!!.value.toDouble()
			val y = groups["y"]!!.value.toDouble()
			val z = groups["z"]!!.value.toDouble()
			val location = NobaVec(x, y, z).roundToBlock()

			val info = groups["info"]?.value?.take(32) ?: ""

			val text = "$username$info"
			waypoints.add(Waypoint(location, text, Timestamp.now(), config.expirationTime.seconds))
			ChatUtils.addMessage(tr("nobaaddons.temporaryWaypoint.createdFromChat", "Temporary Waypoint added at x: $x, y: $y, z: $z from $username"))
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		val cameraPos = context.camera().pos.toNobaVec()
		val color = config.waypointColor.toNobaColor()

		waypoints.removeIf { it.expired || it.location.distance(cameraPos) < 5.0 }
		waypoints.forEach { waypoint ->
			waypoint.location.let {
				val distance = it.distanceToPlayer()
				val formattedDistance = distance.toInt().addSeparators()

				RenderUtils.renderWaypoint(context, it, color, throughBlocks = true)
				RenderUtils.renderText(it.center().raise(), waypoint.text, color, yOffset = -10.0f, hideThreshold = 5.0, throughBlocks = true)
				RenderUtils.renderText(it.center().raise(), "${formattedDistance}m", NobaColor.GRAY, hideThreshold = 5.0, throughBlocks = true)
			}
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