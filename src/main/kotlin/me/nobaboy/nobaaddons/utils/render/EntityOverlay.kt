package me.nobaboy.nobaaddons.utils.render

import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalNotification
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.render.OverlayTexture
import net.minecraft.entity.Entity
import java.awt.Color

object EntityOverlay {
	var overlay: OverlayTexture? = null
		private set

	private val entities = CacheBuilder.newBuilder()
		.weakKeys()
		.removalListener(this::teardown)
		.build<Entity, TintOverlayTexture>()

	init {
		EntityEvents.PRE_RENDER.register { overlay = entities.getIfPresent(it.entity) }
		EntityEvents.DESPAWN.register { entities.invalidate(it.entity) }
	}

	// TODO remove this
	@Deprecated("Use of AWT Color is deprecated, and is converted to NobaColor internally")
	fun Entity.highlight(color: Color) {
		set(this, color.toNobaColor())
	}

	fun Entity.highlight(color: NobaColor) {
		set(this, color)
	}

	operator fun get(entity: Entity): NobaColor? = entities.getIfPresent(entity)?.lastColor
	operator fun contains(entity: Entity): Boolean = entities.getIfPresent(entity) != null

	operator fun set(entity: Entity, color: NobaColor) {
		if(FabricLoader.getInstance().isModLoaded("iris")) return // TODO figure out how to make this play nicely with iris
		val overlay: TintOverlayTexture = run {
			val overlay = entities.getIfPresent(entity)
			if(overlay == null) {
				val created = TintOverlayTexture()
				entities.put(entity, created)
				created
			} else {
				overlay
			}
		}
		overlay.setColor(color)
	}

	private fun teardown(notification: RemovalNotification<Entity, TintOverlayTexture>) {
		notification.value?.close()
	}

	fun remove(entity: Entity) {
		entities.invalidate(entity)
	}
}