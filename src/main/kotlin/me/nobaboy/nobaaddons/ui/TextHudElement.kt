package me.nobaboy.nobaaddons.ui

import me.nobaboy.nobaaddons.ui.data.ElementPosition
import me.nobaboy.nobaaddons.ui.data.TextElement
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.drawOutlinedText
import me.nobaboy.nobaaddons.utils.render.RenderUtils.drawText
import me.nobaboy.nobaaddons.utils.render.RenderUtils.getWidth
import me.nobaboy.nobaaddons.utils.render.RenderUtils.scaled
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.math.roundToInt

/**
 * Generic text display [HudElement], providing helper methods to render text
 * on screen.
 */
abstract class TextHudElement(private val element: TextElement) : HudElement() {
	override val elementPosition: ElementPosition get() = element.position

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
	 * @param allowAlignment If `false`, [alignment] will be entirely ignored.
	 * @param alignment Overrides the class-level text alignment for the current line
	 */
	protected fun renderLine(
		context: DrawContext,
		text: Text,
		x: Int = 0,
		y: Int = 0,
		line: Int = 0,
		allowAlignment: Boolean = true,
		alignment: ElementAlignment? = null,
	) {
		require(line >= 0) { "line must be zero or a positive integer" }

		val x = this.x + x + run {
			if(!allowAlignment) return@run 0
			val size = scaledSize
			val textWidth = if(allowScaling) (text.getWidth() * scale).roundToInt() else text.getWidth()
			when(alignment ?: this.alignment) {
				ElementAlignment.LEFT -> 0
				ElementAlignment.CENTER -> (size.first - textWidth) / 2 + 1
				ElementAlignment.RIGHT -> size.first - textWidth
			}
		}
		val y = (this.y + y + line * (MCUtils.textRenderer.fontHeight + 1) * scale).toInt()

		when(textShadow) {
			TextShadow.OUTLINE -> context.drawOutlinedText(text, x, y, scale, NobaColor(color), NobaColor(outlineColor), applyScaling = false)
			TextShadow.NONE, TextShadow.SHADOW -> {
				val shadow = textShadow == TextShadow.SHADOW
				context.drawText(text, x, y, scale, NobaColor(color), shadow, applyScaling = false)
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
		context.scaled(scale) {
			renderText(context)
		}
	}

	/**
	 * Utility method that returns the total bounds for the given list of [Text] objects
	 */
	fun getBoundsFrom(text: List<Text>): Pair<Int, Int> {
		val toCompare = text + listOf(name)
		val width = toCompare.maxOf { it.getWidth() }
		val height = text.count().coerceAtLeast(1) * (MCUtils.textRenderer.fontHeight + 1)
		return (width * scale).toInt() to (height * scale).toInt()
	}
}