package me.nobaboy.nobaaddons.features.ui

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.ui.controllers.InfoBox
import me.nobaboy.nobaaddons.config.ui.elements.HudElement
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import java.util.LinkedHashMap
import kotlin.collections.set

object ElementManager : LinkedHashMap<String, HudElement>() {
	private fun readResolve(): Any = ElementManager

	private val config get() = NobaConfigManager.get().userInterface

	fun init() {
		loadElements()

		HudRenderCallback.EVENT.register { context, tickCounter ->
			this.forEach {
				it.value.render(context, false, false)
			}
		}
	}

	fun add(element: HudElement) {
		this[element.identifier] = element
	}

	fun loadElements() {
		clear()
		config.infoBoxes.forEach {
			val infoBox = InfoBox(it.text, it.mode, it.element)
			add(InfoBoxHud(infoBox))
		}
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