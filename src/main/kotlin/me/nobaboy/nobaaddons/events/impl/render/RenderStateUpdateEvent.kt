package me.nobaboy.nobaaddons.events.impl.render

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.render.EntityDataKey
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity

/**
 * Event invoked when an [entity]'s [render state][state] is updated
 *
 * This event is invoked after all applicable vanilla code has modified the render state.
 */
data class RenderStateUpdateEvent(val entity: Entity, val state: EntityRenderState) : Event {
	/**
	 * Get the value stored under the provided [key] from the [entity]
	 */
	fun <T> getEntity(key: EntityDataKey<T>): T = key.get(entity)

	/**
	 * Get the value stored under the provided [key] from the [render state][state]
	 */
	fun <T> getState(key: EntityDataKey<T>): T = key.get(state)

	/**
	 * Copy the value stored under the provided [key] from the [entity] to the [render state][state]
	 */
	fun <T> copyToRender(key: EntityDataKey<T>) {
		key.copyToRender(entity, state)
	}

	/**
	 * Set the stored value under the provided [key] on the [render state][state]
	 */
	fun <T> set(key: EntityDataKey<T>, value: T) {
		key.put(state, value)
	}

	companion object {
		@JvmField val EVENT = EventDispatcher<RenderStateUpdateEvent>()
	}
}
