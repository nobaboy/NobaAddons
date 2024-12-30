package me.nobaboy.nobaaddons.ui

import com.google.common.collect.ForwardingSet
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import me.nobaboy.nobaaddons.ui.elements.HudElement
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxesManager
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext

object ElementManager : ForwardingSet<HudElement>() {
	private val elements = mutableSetOf<HudElement>()

	fun init() {
		loadElements()
		HudRenderCallback.EVENT.register { context, _ -> render(context) }
	}

	private fun render(ctx: DrawContext) {
		asSequence()
			.filter { it.enabled }
			.forEach {
				runCatching { it.render(ctx) }
					.onFailure { error ->
						ErrorManager.logError(
							"HUD element threw an error while attempting to render", error, "Element" to it::class
						)
					}
			}
	}

	fun saveAll() {
		InfoBoxesManager.save()
	}

	fun loadElements() {
		clear()
		InfoBoxesManager.infoBoxes.forEach { add(InfoBoxHud(it))}
	}

	override fun delegate(): Set<HudElement> = elements
}