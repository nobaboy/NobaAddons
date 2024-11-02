package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

object MineshaftWaypoints {
	private val config get() = NobaConfigManager.config.mining.glaciteMineshaft

	private const val BLOCKS_FORWARD: Int = 7

	val waypoints = mutableListOf<Waypoint>()

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register(this::handleIslandChangeEvent)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun handleIslandChangeEvent(island: IslandType) {
		waypoints.clear()

		if(island != IslandType.MINESHAFT) return
		val blockBelow = NobaVec.blockBelowPlayer().roundToBlock()

		if(config.entranceWaypoint) waypoints.add(Waypoint(blockBelow, "Entrance", NobaColor.YELLOW))
		if(config.ladderWaypoint) {
			val facing = MCUtils.player?.rotationVector?.toNobaVec()?.round(0) ?: return
			val vec = blockBelow
				// Move 7 blocks in front of the player to be in the ladder shaft
				.add(x = facing.x * BLOCKS_FORWARD, z = facing.z * BLOCKS_FORWARD)
				// Adjust 2 blocks to the right to be in the center of the ladder shaft
				.add(x = facing.z * -2, z = facing.x * 2)
				// Move 15 blocks down to be at the bottom of the ladder shaft
				.add(y = -15)

			waypoints.add(Waypoint(vec, "Ladder", NobaColor.YELLOW))
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(waypoints.isEmpty()) return

		waypoints.forEach {
			val shouldRender = when {
				it.isCorpse -> config.corpseLocator
				it.text == "Entrance" -> config.entranceWaypoint
				it.text == "Ladder" -> config.ladderWaypoint
				else -> false
			}

			if(!shouldRender) return
			RenderUtils.drawOutlinedWaypoint(context, it.vec, it.color.toColor(), throughBlocks = true)
			RenderUtils.drawText(context, it.vec.add(x = 0.5, y = 1.5, z = 0.5), it.text)
		}
	}
}