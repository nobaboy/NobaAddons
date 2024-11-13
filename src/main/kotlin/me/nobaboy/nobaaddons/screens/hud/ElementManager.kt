package me.nobaboy.nobaaddons.screens.hud

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import me.nobaboy.nobaaddons.screens.hud.elements.HudElement
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import java.util.LinkedHashMap
import kotlin.collections.set

object ElementManager : LinkedHashMap<String, HudElement>() {
	private val config get() = NobaConfigManager.config.uiAndVisuals

	fun init() {
		loadElements()

		HudRenderCallback.EVENT.register { context, _ ->
			this.forEach { it.value.render(context) }
		}
	}

	fun add(element: HudElement) {
		this[element.identifier] = element
	}

	fun loadElements() {
		clear()

		config.infoBoxes.forEach { add(InfoBoxHud(it)) }
	}

	fun newIdentifier(base: String): String {
		var identifier: String
		var i: Int = 1
		do {
			identifier = "$base ${i++}"
		} while (identifier in this)
		return identifier
	}
}