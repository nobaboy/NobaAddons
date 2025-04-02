package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.text.Text

object MineshaftWaypoints {
	private val config get() = NobaConfig.mining.glaciteMineshaft

	val waypoints = mutableListOf<MineshaftWaypoint>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register(this::onIslandChange)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onIslandChange(event: SkyBlockEvents.IslandChange) {
		waypoints.clear()

		if(event.island != SkyBlockIsland.MINESHAFT) return
		val blockBelow = LocationUtils.blockBelowPlayer().roundToBlock()

		if(config.entranceWaypoint) addWaypoint(blockBelow, tr("nobaaddons.mineshaftWaypoints.entrance", "Entrance"), NobaColor.BLUE, MineshaftWaypointType.ENTRANCE)
		if(config.ladderWaypoint) {
			val facing = MCUtils.player?.rotationVector?.toNobaVec()?.round(0) ?: return
			val location = blockBelow
				// Move 7 blocks in front of the player to be in the ladder shaft
				.add(x = facing.x * 7, z = facing.z * 7)
				// Adjust 2 blocks to the right to be in the center of the ladder shaft
				.add(x = facing.z * -2, z = facing.x * 2)
				// Move 15 blocks down to be at the bottom of the ladder shaft
				.add(y = -15)

			addWaypoint(location, tr("nobaaddons.mineshaftWaypoints.ladder", "Ladder"), NobaColor.BLUE, MineshaftWaypointType.LADDER)
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(waypoints.isEmpty()) return

		waypoints.forEach {
			val shouldRender = when(it.type) {
				MineshaftWaypointType.ENTRANCE -> config.entranceWaypoint
				MineshaftWaypointType.LADDER -> config.ladderWaypoint
				MineshaftWaypointType.CORPSE -> config.corpseLocator
			}

			if(!shouldRender) return
			RenderUtils.renderWaypoint(context, it.location, it.color, throughBlocks = true)
			RenderUtils.renderText(context, it.location.center().raise(), it.text, yOffset = -5f, hideThreshold = 5.0, throughBlocks = true)
		}
	}

	fun addWaypoint(location: NobaVec, text: Text, color: NobaColor, type: MineshaftWaypointType) {
		waypoints.add(MineshaftWaypoint(location, text, color, type))
	}
}