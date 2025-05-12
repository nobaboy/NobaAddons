package me.nobaboy.nobaaddons.features.dungeons

import me.nobaboy.nobaaddons.api.skyblock.DungeonsAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.HighlightMode
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity

// TODO: Rework and implement Entity outlines
object HighlightStarredMobs {
	private val config get() = NobaConfig.dungeons.highlightStarredMobs
	private val enabled: Boolean get() = config.enabled && !DungeonsAPI.inBoss()

	private val starredMobs = mutableListOf<ArmorStandEntity>()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { starredMobs.clear() }
		TickEvents.TICK.register { getStarredMobs() }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderHighlights)
	}

	private fun getStarredMobs() {
		if(!enabled) return

		EntityUtils.getEntities<ArmorStandEntity>().filter {
			it !in starredMobs &&
			it.hasCustomName() &&
			it.shouldRenderName() &&
			it.customName!!.string.cleanFormatting().startsWith("✯ ") &&
			it.customName!!.string.cleanFormatting().endsWith("❤")
		}.forEach { starredMobs.add(it) }
	}

	private fun renderHighlights(context: WorldRenderContext) {
		if(!enabled) return

		val player = MCUtils.player ?: return

		val color = config.highlightColor
		val mode = config.highlightMode

		starredMobs.removeIf { !it.isAlive }
		for(starredMob in starredMobs) {
			if(!player.canSee(starredMob)) continue

			val name = starredMob.customName!!.string.cleanFormatting()
			val extraHeight = if("Fels" in name) 2.0 else if("Spider" in name) -0.25 else 1.0

			val vec = starredMob.getNobaVec()

			when(mode) {
				HighlightMode.OUTLINE -> RenderUtils.renderOutline(context, vec.add(x = -0.5, y = -1.0, z = -0.5), color, extraSizeBottomY = extraHeight)
				HighlightMode.FILLED -> RenderUtils.renderFilledBox(context, vec.add(x = -0.5, y = -1.0, z = -0.5), color, extraSizeBottomY = extraHeight)
				HighlightMode.FILLED_OUTLINE -> RenderUtils.renderOutlinedFilledBox(context, vec.add(x = -0.5, y = -1.0, z = -0.5), color, extraSizeBottomY = extraHeight)
			}
		}
	}
}