package me.nobaboy.nobaaddons.config.ui

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.controllers.infobox.InfoBox
import me.nobaboy.nobaaddons.config.ui.elements.HudElement
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import java.util.UUID

object ElementManager : LinkedHashMap<String, HudElement>() {
	private val config get() = NobaConfigManager.get().userInterface
	private fun readResolve(): Any = ElementManager

	fun addElement(element: HudElement) {
		this[element.identifier] = element
	}

	fun newIdentifier(base: String): String {
		var identifier: String
		do {
			val uuid = UUID.randomUUID().toString()
			val hex = uuid.split("-")[0]
			identifier = "$base#$hex"
		} while (identifier in this)
		return identifier
	}

	fun loadElements() {
		clear()
		config.infoBoxes.forEach {
			val infoBox = InfoBox(it.text, it.identifier, it.x, it.y, it.scale)
			addElement(InfoBoxHud(infoBox))
		}
	}

	fun getTotalElements(): Int {
		val totalCount = config.infoBoxes.size
		return totalCount
	}

	fun init() {
		HudRenderCallback.EVENT.register { context, _ ->
			if(this.size != getTotalElements()) loadElements()
			this.forEach {
				it.value.render(context, false, false)
			}
		}
	}
}