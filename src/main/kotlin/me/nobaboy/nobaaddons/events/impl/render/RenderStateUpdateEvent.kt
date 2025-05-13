package me.nobaboy.nobaaddons.events.impl.render

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.render.state.RenderStateDataKey
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity

/**
 * Event invoked when an [entity]'s [render state][state] is updated
 *
 * This event is invoked after all applicable vanilla code has modified the render state.
 */
data class RenderStateUpdateEvent(val entity: Entity, val state: EntityRenderState) : Event {
	fun <T> getEntity(key: RenderStateDataKey<T>): T = key.get(entity)
	fun <T> getState(key: RenderStateDataKey<T>): T = key.get(state)

	fun <T> copyToRender(key: RenderStateDataKey<T>) {
		key.copyToRender(entity, state)
	}

	fun <T> set(key: RenderStateDataKey<T>, value: T) {
		key.put(state, value)
	}

	companion object {
		@JvmField val EVENT = EventDispatcher<RenderStateUpdateEvent>()
	}
}
