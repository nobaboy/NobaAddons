package me.nobaboy.nobaaddons.ui

import com.google.common.collect.ForwardingSet
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext

/**
 * UI manager, handles rendering all added [HudElement]s
 */
object UIManager : ForwardingSet<HudElement>() {
	private val elements = mutableSetOf<HudElement>()

	fun init() {
		HudRenderCallback.EVENT.register { context, _ -> render(context) }
	}

	private fun render(ctx: DrawContext) {
		for(element in this) {
			try {
				if(!element.shouldRender()) continue
				element.render(ctx)
			} catch(error: Throwable) {
				ErrorManager.logError(
					"HUD element threw an error while attempting to render", error, "Element" to element::class
				)
			}
		}
	}

	override fun delegate(): Set<HudElement> = elements
}