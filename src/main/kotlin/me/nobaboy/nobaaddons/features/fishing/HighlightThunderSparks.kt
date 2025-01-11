package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.EntityEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.skullFromRepo
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockStateAt
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkullTexture
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.fluid.LavaFluid

object HighlightThunderSparks {
	private val config get() = NobaConfig.INSTANCE.fishing.highlightThunderSparks
	private val enabled: Boolean get() = config.enabled && SkyBlockIsland.CRIMSON_ISLE.inIsland()

	private val THUNDER_SPARK_TEXTURE by "ewogICJ0aW1lc3RhbXAiIDogMTY0MzUwNDM3MjI1NiwKICAicHJvZmlsZUlkIiA6ICI2MzMyMDgwZTY3YTI0Y2MxYjE3ZGJhNzZmM2MwMGYxZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZWFtSHlkcmEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2IzMzI4ZDNlOWQ3MTA0MjAzMjI1NTViMTcyMzkzMDdmMTIyNzBhZGY4MWJmNjNhZmM1MGZhYTA0YjVjMDZlMSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9".skullFromRepo("thunder_spark")
	private val thunderSparks = mutableListOf<ArmorStandEntity>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { thunderSparks.clear() }
		EntityEvents.POST_RENDER.register(this::onEntityRender)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderHighlights)
	}

	private fun onEntityRender(event: EntityEvents.Render) {
		if(!enabled) return

		val entity = event.entity as? ArmorStandEntity ?: return
		if(entity in thunderSparks) return
		if(entity.mainHandStack.getSkullTexture() != THUNDER_SPARK_TEXTURE) return

		thunderSparks.add(entity)
	}

	private fun renderHighlights(context: WorldRenderContext) {
		if(!enabled) return

		thunderSparks.removeIf { !it.isAlive }
		thunderSparks.forEach {
			val vec = it.getNobaVec()
			val block = vec.roundToBlock().add(y = 1).getBlockStateAt()
			val throughBlocks = vec.distanceToPlayer() < 6 && block.fluidState != null && block.fluidState.fluid is LavaFluid

			RenderUtils.renderOutlinedFilledBox(context, vec.add(x = -0.5, z = -0.5), config.highlightColor, extraSize = -0.25, throughBlocks = throughBlocks)
			if(config.showText && vec.distanceToPlayer() < 10) RenderUtils.renderText(vec.raise(1.25), "Thunder Spark", throughBlocks = throughBlocks)
		}
	}
}