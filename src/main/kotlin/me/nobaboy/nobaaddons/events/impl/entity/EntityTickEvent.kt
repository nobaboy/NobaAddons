package me.nobaboy.nobaaddons.events.impl.entity

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.entity.Entity

/**
 * Event invoked when *any* entity ticks on the client
 */
data class EntityTickEvent(val entity: Entity) : Event {
	companion object {
		@JvmField val EVENT = EventDispatcher<EntityTickEvent>()
	}
}
