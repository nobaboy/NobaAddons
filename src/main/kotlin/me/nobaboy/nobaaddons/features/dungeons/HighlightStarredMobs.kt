package me.nobaboy.nobaaddons.features.dungeons

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.HighlightMode
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity

// TODO: Rework and implement Entity outlines
object HighlightStarredMobs {
	private val config get() = NobaConfigManager.config.dungeons.highlightStarredMobs

	private val starredMobs = mutableListOf<ArmorStandEntity>()

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { _ -> starredMobs.clear() }
		WorldRenderEvents.AFTER_TRANSLUCENT.register { context -> renderHighlights(context) }
		ClientTickEvents.END_CLIENT_TICK.register { getStarredMobs() }
	}

	private fun getStarredMobs() {
		if(!isEnabled()) return

		EntityUtils.getEntities<ArmorStandEntity>().filter {
			it !in starredMobs &&
			it.hasCustomName() &&
			it.shouldRenderName() == true &&
			it.customName!!.string.cleanFormatting().startsWith("✯ ") &&
			it.customName!!.string.cleanFormatting().endsWith("❤")
		}.forEach { starredMobs.add(it) }
	}

	private fun renderHighlights(context: WorldRenderContext) {
		if(!isEnabled()) return

		val player = MCUtils.player ?: return

		val color = config.highlightColor
		val mode = config.highlightMode

		starredMobs.removeIf { !it.isAlive }
		for(starredMob in starredMobs) {
			if(!player.canSee(starredMob)) continue

			val name = starredMob.customName!!.string.cleanFormatting()
			var height = if("Fels" in name) 2.0 else if("Withermancer" in name) 1.4 else if("Spider" in name) 0.75 else 1.0

			val vec = starredMob.getNobaVec()

			when(mode) {
				HighlightMode.OUTLINE -> RenderUtils.drawOutline(context, vec.add(x = -0.5, y = -1.0, z = -0.5), color, extraSizeBottomY = height)
				HighlightMode.FILLED -> RenderUtils.drawFilledBox(context, vec.add(x = -0.5, y = -1.0, z = -0.5), color, extraSizeBottomY = height)
				HighlightMode.FILLED_OUTLINE -> RenderUtils.drawOutlinedFilledBox(context, vec.add(x = -0.5, y = -1.0, z = -0.5), color, extraSizeBottomY = height)
			}
		}
	}

	private fun isEnabled() = /*IslandType.DUNGEONS.inIsland() && !DungeonsAPI.inBoss() && config.enabled*/ false
}