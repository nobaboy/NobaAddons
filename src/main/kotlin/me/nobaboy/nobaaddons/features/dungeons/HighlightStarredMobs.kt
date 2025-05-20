package me.nobaboy.nobaaddons.features.dungeons

import me.nobaboy.nobaaddons.api.skyblock.DungeonsAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.HighlightMode
import me.nobaboy.nobaaddons.utils.render.RenderUtils.renderFilled
import me.nobaboy.nobaaddons.utils.render.RenderUtils.renderFullBox
import me.nobaboy.nobaaddons.utils.render.RenderUtils.renderOutline
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity

@Module
object HighlightStarredMobs {
	private val config get() = NobaConfig.dungeons.highlightStarredMobs
	private val enabled: Boolean get() = config.enabled && !DungeonsAPI.inBoss()

	private val starredMobs = mutableListOf<LivingEntity>()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { starredMobs.clear() }
		EntityEvents.POST_RENDER.register(this::onEntityRender)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onWorldRender)
	}

	private fun onEntityRender(event: EntityEvents.Render) {
		if(!enabled) return

		val entity = event.entity as? LivingEntity ?: return
		if(entity in starredMobs) return

		val armorStand = EntityUtils.getNextEntity(entity, 1) as? ArmorStandEntity ?: return
		if(MCUtils.player?.canSee(armorStand) != true) return
		if(!armorStand.shouldRenderName() || !armorStand.hasCustomName()) return

		val armorStandName = armorStand.name.string
		if(!armorStandName.startsWith("✯") || !armorStandName.endsWith("❤")) return

		starredMobs.add(entity)
	}

	private fun onWorldRender(context: WorldRenderContext) {
		if(!enabled) return

		starredMobs.removeIf { !it.isAlive }

		val color = config.highlightColor
		val mode = config.highlightMode

		starredMobs.forEach { mob ->
			when(mode) {
				HighlightMode.OUTLINE -> context.renderOutline(mob.boundingBox, color)
				HighlightMode.FILLED -> context.renderFilled(mob.boundingBox, color)
				HighlightMode.FILLED_OUTLINE -> context.renderFullBox(mob.boundingBox, color)
			}
		}
	}
}