package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.client.world.ClientWorld

object WorldEvents {
	@JvmField val POST_LOAD = EventDispatcher<WorldLoadEvent>()

	data class WorldLoadEvent(val world: ClientWorld) : Event()
}