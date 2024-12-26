package me.nobaboy.nobaaddons.screens.hud

import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import me.nobaboy.nobaaddons.screens.hud.elements.HudElement
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesManager
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import java.util.LinkedHashMap
import kotlin.collections.set

object ElementManager : LinkedHashMap<String, HudElement>() {
	fun init() {
		loadElements()

		HudRenderCallback.EVENT.register { context, _ ->
			values.asSequence().filter { it.enabled }.forEach {
				runCatching { it.render(context) }
					.onFailure { error ->
						ErrorManager.logError(
							"HUD element threw an error while attempting to render", error, "Element" to it::class
						)
					}
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