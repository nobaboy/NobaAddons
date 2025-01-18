package me.nobaboy.nobaaddons.events.impl.render

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack

object ScreenRenderEvents {
	@JvmField
	val DRAW_ITEM = EventDispatcher<DrawItem>()

	data class DrawItem(
		val context: DrawContext,
		val textRenderer: TextRenderer,
		val itemStack: ItemStack,
		val x: Int,
		val y: Int
	) : Event()
}