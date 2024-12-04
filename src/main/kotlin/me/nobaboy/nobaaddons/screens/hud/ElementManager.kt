package me.nobaboy.nobaaddons.screens.hud

import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import me.nobaboy.nobaaddons.screens.hud.elements.HudElement
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesManager
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import java.util.LinkedHashMap
import kotlin.collections.set

object ElementManager : LinkedHashMap<String, HudElement>() {
	fun init() {
		loadElements()

		HudRenderCallback.EVENT.register { context, _ ->
			this.values.forEach { value ->
				value.takeIf { it.enabled }?.render(context)
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
}