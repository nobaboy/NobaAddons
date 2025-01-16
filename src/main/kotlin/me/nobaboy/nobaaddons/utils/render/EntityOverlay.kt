package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.ducks.OverlayTextureDuck
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.minecraft.entity.LivingEntity
import java.awt.Color

object EntityOverlay {
	var overlay = false
		private set

	private val texture by MCUtils.client.gameRenderer::overlayTexture
	private val entities = mutableMapOf<LivingEntity, Color>()

	init {
		EntityEvents.PRE_RENDER.register {
			val color = entities[it.entity] ?: return@register
			(texture as OverlayTextureDuck).`nobaaddons$setColor`(color)
			overlay = true
		}

		EntityEvents.POST_RENDER.register {
			if(!overlay) return@register
			(texture as OverlayTextureDuck).`nobaaddons$setColor`(null)
			overlay = false
		}

		ClientEntityEvents.ENTITY_UNLOAD.register { entity, _ ->
			if(entity is LivingEntity) entities.remove(entity)
		}
	}

	fun LivingEntity.highlight(color: NobaColor, alpha: Int = 175) {
		set(this, color, alpha)
	}

	fun set(entity: LivingEntity, color: NobaColor, alpha: Int = 175) {
		entities[entity] = Color(color.red, color.green, color.blue, alpha)
	}

	fun remove(entity: LivingEntity) {
		entities.remove(entity)
	}
}