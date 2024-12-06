package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

object ScreenRenderEvents {
	@JvmField
	val DRAW_SLOT = EventDispatcher<DrawSlot>()

	data class DrawSlot(val context: DrawContext, val textRenderer: TextRenderer, val slot: Slot) {
		val itemStack: ItemStack by slot::stack
		val x: Int by slot::x
		val y: Int by slot::y
		val lore: LoreComponent? get() = itemStack.lore
	}
}