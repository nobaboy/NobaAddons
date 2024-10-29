package me.nobaboy.nobaaddons.config.ui.elements

import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.getWidth
import net.minecraft.client.gui.DrawContext

abstract class TextHud(element: Element) : HudElement(element) {
	abstract val text: String
	abstract val mode: TextMode
	abstract val outlineColor: Int

	override fun render(context: DrawContext) {
		if(!shouldRender) return

		RenderUtils.startScale(context, scale.toFloat())
		renderLine(context, x, y, scale.toFloat(), color)
		RenderUtils.endScale(context)
	}

	override fun getBounds(): ElementBounds {
		val width = text.getWidth().takeIf { it > 0 } ?: identifier.getWidth()
		return ElementBounds(x, y, (width * scale).toInt(), (MCUtils.textRenderer.fontHeight * scale).toInt() - 1)
	}

	private fun renderLine(context: DrawContext, x: Int, y: Int, scale: Float, color: Int) {
		when(mode) {
			TextMode.PURE -> RenderUtils.drawText(context, text, x, y, scale, color, shadow = false, applyScaling = false)
			TextMode.SHADOW -> RenderUtils.drawText(context, text, x, y, scale, color, shadow = true, applyScaling = false)
			TextMode.OUTLINE -> RenderUtils.drawOutlinedText(context, text, x, y, scale, color, outlineColor, applyScaling = false)
		}
	}
}