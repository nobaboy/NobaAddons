package me.nobaboy.nobaaddons.events.impl.fabric

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents as FabricWorldRenderEvents

object WorldRenderEvents {
	init {
		FabricWorldRenderEvents.AFTER_TRANSLUCENT.register { AFTER_TRANSLUCENT.invoke(AfterTranslucent(it)) }
	}

	val AFTER_TRANSLUCENT = EventDispatcher<AfterTranslucent>()

	data class AfterTranslucent(val ctx: WorldRenderContext) : Event()
}