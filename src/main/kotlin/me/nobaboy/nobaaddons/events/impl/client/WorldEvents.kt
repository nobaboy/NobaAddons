package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.client.world.ClientWorld

object WorldEvents {
	@JvmField val POST_LOAD = EventDispatcher<WorldLoadEvent>()

	data class WorldLoadEvent(val world: ClientWorld) : Event()
}