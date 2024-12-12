package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.CancelableEvent
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.entity.Entity

object EntityRenderEvents {
	/**
	 * Event invoked to determine if a given [Entity] should be allowed to render in the world
	 */
	@JvmField val ALLOW_RENDER = EventDispatcher<AllowRender>()

	/**
	 * Event called before an [Entity] is rendered, before any matrix stack modifications have been made.
	 */
	@JvmField val PRE_RENDER = EventDispatcher<Render>()

	/**
	 * Event called after an [Entity] is rendered, after all matrix stack modifications have been unwound.
	 */
	@JvmField val POST_RENDER = EventDispatcher<Render>()

	data class AllowRender(val entity: Entity) : CancelableEvent()
	data class Render(val entity: Entity, val delta: Float)
}