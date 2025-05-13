package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.CancelableEvent
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity

object EntityEvents {
	init {
		ClientEntityEvents.ENTITY_LOAD.register { entity, _ -> SPAWN.dispatch(Spawn(entity)) }
		ClientEntityEvents.ENTITY_UNLOAD.register { entity, _ -> DESPAWN.dispatch(Despawn(entity)) }
	}

	/**
	 * Event invoked when an [Entity] is spawned.
	 *
	 * This event is a wrapper around the Fabric API event.
	 */
	@JvmField val SPAWN = EventDispatcher<Spawn>()

	/**
	 * Event invoked when an [Entity] is despawned.
	 *
	 * This event is a wrapper around the Fabric API event.
	 */
	@JvmField val DESPAWN = EventDispatcher<Despawn>()

	/**
	 * Event invoked before an [Entity] is rendered, before any matrix stack modifications have been made.
	 */
	@JvmField val PRE_RENDER = EventDispatcher<Render>()

	/**
	 * Event invoked after an [Entity] is rendered, after all matrix stack modifications have been unwound.
	 */
	@JvmField val POST_RENDER = EventDispatcher<Render>()

	/**
	 * Event invoked to determine whether a given [Entity] should be allowed to render in the world.
	 */
	@JvmField val ALLOW_RENDER = EventDispatcher.cancelable<AllowRender>()

	/**
	 * Event invoked when an [Entity]'s vehicle is changed.
	 */
	@JvmField val VEHICLE_CHANGE = EventDispatcher<VehicleChange>()

	data class Spawn(val entity: Entity) : Event
	data class Despawn(val entity: Entity) : Event
	data class Render(val entity: Entity, val state: EntityRenderState, val delta: Float) : Event
	data class AllowRender(val entity: Entity) : CancelableEvent()
	data class VehicleChange(val entity: Entity, val vehicle: Entity) : Event
}