package me.nobaboy.nobaaddons.ui

import com.google.common.collect.ForwardingMap
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import me.nobaboy.nobaaddons.ui.elements.HudElement
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxesManager
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import kotlin.collections.set

object ElementManager : ForwardingMap<String, HudElement>() {
	private val map = mutableMapOf<String, HudElement>()

	fun init() {
		loadElements()
		HudRenderCallback.EVENT.register { context, _ -> render(context) }
	}

	private fun render(ctx: DrawContext) {
		values.asSequence()
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

	fun add(element: HudElement) {
		this[element.identifier] = element
	}

	fun loadElements() {
		clear()
		save()

		InfoBoxesManager.infoBoxes.forEach { add(InfoBoxHud(it))}
	}

	private fun save() {
		InfoBoxesManager.saveInfoBoxes()
	}

	override fun delegate(): Map<String, HudElement> = map
}