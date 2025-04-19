package me.nobaboy.nobaaddons.events.impl.render

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack

object ScreenRenderEvents {
	@JvmField
	val DRAW_ITEM = EventDispatcher<DrawItem>()

	/**
	 * Convenience wrapper around [ScreenEvents.AFTER_INIT] that filters for [T]
	 */
	inline fun <reified T : Screen> afterInit(crossinline init: (MinecraftClient, T, Int, Int) -> Unit) {
		ScreenEvents.AFTER_INIT.register { client, screen, scaledWidth, scaledHeight ->
			if(screen is T) {
				ErrorManager.catching("Screen post-init event errored") {
					init(client, screen, scaledWidth, scaledHeight)
				}
			}
		}
	}

	/**
	 * Convenience wrapper around creating a [ScreenEvents.afterRender] event in [ScreenEvents.AFTER_INIT]
	 * for a screen of [T] type
	 */
	inline fun <reified T : Screen> afterRender(crossinline render: (T, DrawContext, Int, Int, Float) -> Unit) {
		afterInit<T> { _, screen, _, _ ->
			ScreenEvents.afterRender(screen).register { _, ctx, mouseX, mouseY, delta ->
				ErrorManager.catching("Screen render event errored") { render(screen, ctx, mouseX, mouseY, delta) }
			}
		}
	}

	data class DrawItem(
		val context: DrawContext,
		val textRenderer: TextRenderer,
		val itemStack: ItemStack,
		val x: Int,
		val y: Int
	) : Event()
}