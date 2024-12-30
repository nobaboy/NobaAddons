package me.nobaboy.nobaaddons.ui

import me.nobaboy.nobaaddons.ui.data.AbstractTextElement
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.getWidth
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.collections.maxOf
import kotlin.collections.plus

/**
 * Generic text display [HudElement], providing helper methods to render text
 * on screen.
 */
abstract class TextHudElement(element: AbstractTextElement<*>) : HudElement(element.position) {
	open val color: Int by element::color
	open val textShadow: TextShadow by element::textShadow
	open val outlineColor: Int by element::outlineColor

	/**
	 * Calls [renderLine] for each of the provided [lines]
	 */
	protected fun renderLines(context: DrawContext, lines: List<Text>, x: Int = 0, y: Int = 0) {
		lines.forEachIndexed { i, line -> renderLine(context, line, x, y, i) }
	}

	/**
	 * Renders the provided [text] on the given line
	 *
	 * @param x How much to offset the X value for this line
	 * @param y How much to offset the Y value for this line
	 * @param line Automatically adds `line * fontRenderer.fontHeight` to [y]
	 */
	protected fun renderLine(context: DrawContext, text: Text, x: Int = 0, y: Int = 0, line: Int = 0) {
		require(line >= 0) { "line must not be negative" }
		val x = this.x + x + let {
			if(alignment == ElementAlignment.RIGHT) this.size.first - text.getWidth() else 0
		}
		val y = this.y + y + (line * MCUtils.textRenderer.fontHeight)
		when(textShadow) {
			TextShadow.OUTLINE -> RenderUtils.drawOutlinedText(context, text, x, y, scale, color, outlineColor, applyScaling = false)
			TextShadow.NONE, TextShadow.SHADOW -> {
				val shadow = textShadow == TextShadow.SHADOW
				RenderUtils.drawText(context, text, x, y, scale, color, shadow, applyScaling = false)
			}
		}
	}

	/**
	 * Implement this method to render the text for this HUD
	 *
	 * This method is called from within [RenderUtils.scaled]
	 */
	protected abstract fun renderText(context: DrawContext)

	override fun render(context: DrawContext) {
		if(!shouldRender()) return

		RenderUtils.scaled(context, scale) {
			renderText(context)
		}
	}

	/**
	 * Utility method that returns the total bounds for the given list of [Text] objects
	 */
	fun getBoundsFrom(text: List<Text>): Pair<Int, Int> {
		val toCompare = text + listOf(name)
		val width = toCompare.maxOf { it.getWidth() }
		val height = text.count().coerceAtLeast(1) * MCUtils.textRenderer.fontHeight
		return (width * scale).toInt() to (height * scale).toInt()
	}
}