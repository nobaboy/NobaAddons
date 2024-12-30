package me.nobaboy.nobaaddons.ui.elements

import me.nobaboy.nobaaddons.ui.elements.data.ElementBounds
import me.nobaboy.nobaaddons.ui.elements.data.AbstractTextElement
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.getWidth
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

abstract class TextHud(element: AbstractTextElement<*>) : HudElement(element.position) {
	open val color: Int by element::color
	open val textShadow: TextShadow by element::textShadow
	open val outlineColor: Int by element::outlineColor

	protected fun renderLine(context: DrawContext, text: Text, x: Int = 0, y: Int = 0) {
		val x = this.x + x
		val y = this.y + y
		when(textShadow) {
			TextShadow.OUTLINE -> RenderUtils.drawOutlinedText(context, text, x, y, scale, color, outlineColor, applyScaling = false)
			TextShadow.NONE, TextShadow.SHADOW -> {
				val shadow = textShadow == TextShadow.SHADOW
				RenderUtils.drawText(context, text, x, y, scale, color, shadow, applyScaling = false)
			}
		}
	}

	protected abstract fun renderText(context: DrawContext)

	override fun render(context: DrawContext) {
		if(!shouldRender()) return

		RenderUtils.startScale(context, scale)
		renderText(context)
		RenderUtils.endScale(context)
	}

	/**
	 * Utility method that returns the total bounds for the given list of [Text] objects
	 */
	fun getBoundsFrom(text: List<Text>): ElementBounds {
		val toCompare = text + listOf(name)
		val width = toCompare.maxOf { it.getWidth() }
		val height = text.count().coerceAtLeast(1) * MCUtils.textRenderer.fontHeight

		return ElementBounds(x, y, (width * scale).toInt(), (height * scale).toInt())
	}
}