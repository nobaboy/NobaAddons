package me.nobaboy.nobaaddons.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.screen.slot.Slot

object ScreenRenderEvents {
	@JvmField
	val DRAW_SLOT = EventFactory.createArrayBacked(DrawSlotEvent::class.java) { listeners ->
		DrawSlotEvent { context, textRenderer, slot ->
			listeners.forEach { it.onDrawSlot(context, textRenderer, slot) }
		}
	}

	fun interface DrawSlotEvent {
		fun onDrawSlot(context: DrawContext, textRenderer: TextRenderer, slot: Slot)
	}
}