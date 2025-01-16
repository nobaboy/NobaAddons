package me.nobaboy.nobaaddons.events.impl.render

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.entity.Entity

object EntityRenderEvents {
	/**
	 * Event invoked to determine whether a given [Entity] should be allowed to render in the world.
	 */
	@JvmField val ALLOW_RENDER = EventDispatcher<AllowRender>()

	/**
	 * Event invoked before an [Entity] is rendered, before any matrix stack modifications have been made.
	 */
	@JvmField val PRE_RENDER = EventDispatcher<Render>()

	/**
	 * Event invoked after an [Entity] is rendered, after all matrix stack modifications have been unwound.
	 */
	@JvmField val POST_RENDER = EventDispatcher<Render>()

	data class AllowRender(val entity: Entity) : Event(isCancelable = true)
	data class Render(val entity: Entity, val delta: Float) : Event()
}