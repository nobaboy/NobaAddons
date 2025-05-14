package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import me.nobaboy.nobaaddons.utils.properties.Holding
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.client.render.entity.state.LivingEntityRenderState
import net.minecraft.entity.Entity
import java.awt.Color

object EntityOverlay {
	@JvmField val OVERLAY_TEXTURE = EntityDataKey<Holding<TintOverlayTexture>>(::Holding)

	@get:JvmStatic
	var overlay: OverlayTexture? = null
		private set

	init {
		RenderStateUpdateEvent.EVENT.register {
			it.copyToRender(OVERLAY_TEXTURE)
			if(it.state is LivingEntityRenderState && contains(it.state)) {
				it.state.hurt = false
			}
		}
		EntityEvents.PRE_RENDER.register { overlay = OVERLAY_TEXTURE.get(it.entity).get() }
		EntityEvents.POST_RENDER.register { overlay = null }
	}

	// TODO remove this
	@Deprecated("Use of AWT Color is deprecated, and is converted to NobaColor internally")
	fun Entity.highlight(color: Color) {
		set(this, color.toNobaColor())
	}

	fun Entity.highlight(color: NobaColor) {
		set(this, color)
	}

	@JvmStatic
	fun get(entity: Entity): NobaColor? = OVERLAY_TEXTURE.get(entity).get()?.lastColor

	@JvmStatic
	fun get(state: EntityRenderState): NobaColor? = OVERLAY_TEXTURE.get(state).get()?.lastColor

	// java, for whatever reason, inexplicably refuses to acknowledge .get() as a valid method?
	// so the simple fix is to just do this, i guess.
	@JvmStatic
	fun getRgb(state: EntityRenderState): Int? = get(state)?.rgb

	@JvmStatic
	fun contains(entity: Entity): Boolean = OVERLAY_TEXTURE.get(entity).get() != null

	@JvmStatic
	fun contains(entity: EntityRenderState): Boolean = OVERLAY_TEXTURE.get(entity).get() != null

	fun set(entity: Entity, color: NobaColor) {
		if(FabricLoader.getInstance().isModLoaded("iris")) return // TODO figure out how to make this play nicely with iris
		OVERLAY_TEXTURE.get(entity).getOrSet(::TintOverlayTexture).setColor(color)
	}

	fun remove(entity: Entity) {
		OVERLAY_TEXTURE.get(entity).clearWithCleanup(OverlayTexture::close)
	}
}