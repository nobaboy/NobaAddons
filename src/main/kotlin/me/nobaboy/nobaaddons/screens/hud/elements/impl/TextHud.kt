package me.nobaboy.nobaaddons.screens.hud.elements.impl

import me.nobaboy.nobaaddons.screens.hud.elements.HudElement
import me.nobaboy.nobaaddons.screens.hud.elements.TextMode
import me.nobaboy.nobaaddons.screens.hud.elements.data.Element
import me.nobaboy.nobaaddons.screens.hud.elements.data.ElementBounds
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.getWidth
import net.minecraft.client.gui.DrawContext

abstract class TextHud(element: Element) : HudElement(element) {
	abstract val text: String
	abstract val textMode: TextMode
	abstract val outlineColor: Int

	override fun render(context: DrawContext) {
		if(!shouldRender) return

		RenderUtils.startScale(context, scale)
		renderLine(context)
		RenderUtils.endScale(context)
	}

	override fun getBounds(): ElementBounds {
		val identifierWidth = identifier.getWidth()
		val width = text.getWidth().takeIf { it > 0 && it > identifierWidth } ?: identifierWidth
		val height = MCUtils.textRenderer.fontHeight - 1

		return ElementBounds(x, y, (width * scale).toInt(), (height * scale).toInt())
	}

	private fun renderLine(context: DrawContext) {
		when(textMode) {
			TextMode.OUTLINE -> RenderUtils.drawOutlinedText(context, text, x, y, scale, color, outlineColor, applyScaling = false)
			TextMode.PURE, TextMode.SHADOW -> {
				val shadow = textMode == TextMode.SHADOW
				RenderUtils.drawText(context, text, x, y, scale, color, shadow, applyScaling = false)
			}
		}
	}
}