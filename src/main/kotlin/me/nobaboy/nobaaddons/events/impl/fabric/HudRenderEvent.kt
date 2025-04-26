package me.nobaboy.nobaaddons.events.impl.fabric

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

data class HudRenderEvent(val ctx: DrawContext, val delta: RenderTickCounter) : Event {
	companion object {
		init {
			HudRenderCallback.EVENT.register { ctx, delta -> EVENT.dispatch(HudRenderEvent(ctx, delta)) }
		}

		val EVENT = EventDispatcher<HudRenderEvent>()
	}
}