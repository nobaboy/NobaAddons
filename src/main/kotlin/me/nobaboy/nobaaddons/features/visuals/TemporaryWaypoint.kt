package me.nobaboy.nobaaddons.features.visuals

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.findMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import java.util.regex.Pattern
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object TemporaryWaypoint {
	private val config get() = NobaConfigManager.config.uiAndVisuals.temporaryWaypoints

	private val chatCoordsPattern = Pattern.compile(
		"(?i)(?<username>[A-z0-9_]+): x: (?<x>[0-9.-]+),? y: (?<y>[0-9.-]+),? z: (?<z>[0-9.-]+)(?<info>.*)"
	)
	private val waypoints = mutableListOf<Waypoint>()

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { waypoints.clear() }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
		ClientReceiveMessageEvents.GAME.register { message, _ -> handleChatEvent(message.string.cleanFormatting()) }
	}

	fun addWaypoint(ctx: CommandContext<FabricClientCommandSource>): Int {
		if(!isEnabled()) return 0

		val x = DoubleArgumentType.getDouble(ctx, "x")
		val y = DoubleArgumentType.getDouble(ctx, "y")
		val z = DoubleArgumentType.getDouble(ctx, "z")

		waypoints.add(Waypoint(NobaVec(x, y, z), "Temporary Waypoint", Timestamp.currentTime(), null))
		ChatUtils.addMessage("Temporary Waypoint added at x: $x, y: $y, z: $z")
		return Command.SINGLE_SUCCESS
	}

	private fun handleChatEvent(message: String) {
		if(!isEnabled()) return

		chatCoordsPattern.findMatcher(message) {
			val username = group("username")
			if(username == MCUtils.playerName) return

			val x = group("x").toDouble()
			val y = group("y").toDouble()
			val z = group("z").toDouble()
			val info = group("info").take(24)

			val text = "$username$info"
			waypoints.add(Waypoint(NobaVec(x, y, z), text, Timestamp.currentTime(), config.expirationTime.seconds))
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!isEnabled()) return

		val cameraPos = context.camera().pos.toNobaVec()
		val color = config.waypointColor

		waypoints.removeIf { it.expired || it.vec.distanceSq(cameraPos) < 5.0 * 5.0 }
		waypoints.forEach { waypoint ->
			waypoint.vec.roundToBlock().let {
				RenderUtils.renderWaypoint(context, it, color, throughBlocks = true)
				RenderUtils.renderText(context, it.center(), waypoint.text)
			}
		}
	}

	data class Waypoint(val vec: NobaVec, val text: String, val timestamp: Timestamp, val duration: Duration?) {
		val expired: Boolean
			get() = duration != null && timestamp.elapsedSince() >= duration
	}

	private fun isEnabled() = SkyBlockAPI.inSkyblock && config.enabled
}