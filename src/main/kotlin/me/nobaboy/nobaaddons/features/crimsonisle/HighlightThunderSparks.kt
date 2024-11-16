package me.nobaboy.nobaaddons.features.crimsonisle

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockStateAt
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.EntityUtils.heldSkullTexture
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.fluid.LavaFluid

object HighlightThunderSparks {
	private val config get() = NobaConfigManager.config.crimsonIsle.highlightThunderSparks

	private const val TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTY0MzUwNDM3MjI1NiwKICAicHJvZmlsZUlkIiA6ICI2MzMyMDgwZTY3YTI0Y2MxYjE3ZGJhNzZmM2MwMGYxZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZWFtSHlkcmEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2IzMzI4ZDNlOWQ3MTA0MjAzMjI1NTViMTcyMzkzMDdmMTIyNzBhZGY4MWJmNjNhZmM1MGZhYTA0YjVjMDZlMSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
	private val sparks = mutableListOf<ArmorStandEntity>()

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { sparks.clear() }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderHighlights)
		ClientTickEvents.END_CLIENT_TICK.register { getThunderSparks() }
	}

	private fun getThunderSparks() {
		if(!isEnabled()) return

		EntityUtils.getEntities<ArmorStandEntity>().filter {
			it !in sparks && it.heldSkullTexture(TEXTURE)
		}.forEach { sparks.add(it) }
	}

	private fun renderHighlights(context: WorldRenderContext) {
		if(!isEnabled()) return

		val color = config.highlightColor

		sparks.removeIf { !it.isAlive }
		sparks.forEach {
			val vec = it.getNobaVec()
			val block = vec.center().getBlockStateAt()
			val throughBlocks = vec.distanceToPlayer() < 6 && block.fluidState != null && block.fluidState.fluid is LavaFluid

			RenderUtils.renderOutlinedFilledBox(context, vec.add(x = -0.5, z = -0.5), color, extraSize = -0.25, throughBlocks = throughBlocks)
			if(config.showText && vec.distanceToPlayer() < 10) RenderUtils.renderText(context, vec.raise(1.25), "Thunder Spark", hideThreshold = 0.0, throughBlocks = throughBlocks)
		}
	}

	private fun isEnabled() = IslandType.CRIMSON_ISLE.inIsland() && config.enabled
}