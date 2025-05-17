package me.nobaboy.nobaaddons.events.impl.render

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity

/**
 * Event invoked when the [render state][state] for a given [entity] is updated.
 *
 * This event is invoked after the state has been fully updated by vanilla code, but before being returned to the renderer.
 */
data class RenderStateUpdateEvent(val entity: Entity, val state: EntityRenderState) : Event {
	companion object {
		@JvmField val EVENT = EventDispatcher<RenderStateUpdateEvent>()
	}
}
//?}