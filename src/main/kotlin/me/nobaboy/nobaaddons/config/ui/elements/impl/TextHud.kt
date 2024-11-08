package me.nobaboy.nobaaddons.config.ui.elements.impl

import me.nobaboy.nobaaddons.config.ui.elements.Element
import me.nobaboy.nobaaddons.config.ui.elements.ElementBounds
import me.nobaboy.nobaaddons.config.ui.elements.HudElement
import me.nobaboy.nobaaddons.config.ui.elements.TextMode
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.getWidth
import net.minecraft.client.gui.DrawContext

abstract class TextHud(element: Element) : HudElement(element) {
	open val text: String = ""
	open val mode: TextMode = TextMode.SHADOW
	open val outlineColor: Int = 0x000000

	override fun render(context: DrawContext) {
		if(!shouldRender) return

		RenderUtils.startScale(context, scale)
		renderLine(context)
		RenderUtils.endScale(context)
	}

	override fun getBounds(): ElementBounds {
		val width = text.getWidth().takeIf { it > 0 } ?: identifier.getWidth()
		val height = MCUtils.textRenderer.fontHeight - 1
		return ElementBounds(x, y, (width * scale).toInt(), (height * scale).toInt())
	}

	private fun renderLine(context: DrawContext) {
		val shadow = mode == TextMode.SHADOW
		when(mode) {
			TextMode.PURE, TextMode.SHADOW -> RenderUtils.drawText(context, text, x, y, scale, color, shadow, applyScaling = false)
			TextMode.OUTLINE -> RenderUtils.drawOutlinedText(context, text, x, y, scale, color, outlineColor, applyScaling = false)
		}
	}
}