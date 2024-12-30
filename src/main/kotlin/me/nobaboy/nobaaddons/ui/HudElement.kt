package me.nobaboy.nobaaddons.ui

import me.nobaboy.nobaaddons.screens.NobaHudScreen
import me.nobaboy.nobaaddons.ui.data.ElementBounds
import me.nobaboy.nobaaddons.ui.data.ElementPosition
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.math.roundToInt

abstract class HudElement(protected val elementPosition: ElementPosition) {
	var x: Int
		get() = scale(MCUtils.window.scaledWidth, elementPosition.x)
		set(value) { elementPosition.x = value.toDouble() / MCUtils.window.scaledWidth }

	var y: Int
		get() = scale(MCUtils.window.scaledHeight, elementPosition.y)
		set(value) { elementPosition.y = value.toDouble() / MCUtils.window.scaledHeight }

	val alignment: ElementAlignment
		get() = if(elementPosition.x > 0.5) ElementAlignment.RIGHT else ElementAlignment.LEFT

	private fun scale(of: Int, from: Double): Int = (of * from).toInt().coerceIn(0, of)

	open var scale: Float by elementPosition::scale

	open val minScale: Float = 0.5f
	open val maxScale: Float = 3.0f

	abstract val name: Text
	abstract val size: Pair<Int, Int>

	abstract fun render(context: DrawContext)

	open fun getBounds(): ElementBounds = ElementBounds(x, y, size)

	private val scaleOffset: Int get() = (1 * scale).roundToInt().coerceAtLeast(1)

	private val maxScreenBounds: Pair<Int, Int>
		get() {
			val (screenWidth, screenHeight) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }
			val bounds = getBounds()
			val offset = scaleOffset

			val xMax = (screenWidth - bounds.width - offset - 1).coerceAtLeast(1)
			val yMax = (screenHeight - bounds.height - offset - 1).coerceAtLeast(1)
			return xMax to yMax
		}

	open fun shouldRender(): Boolean {
		val client = MCUtils.client
		return !client.options.hudHidden && client.currentScreen !is NobaHudScreen
	}

	open fun renderBackground(context: DrawContext, hovered: Boolean) {
		val color = if(hovered) 0xFF85858A else 0xFF343738

		val offset = (1 * scale).roundToInt().coerceAtLeast(1)
		val bounds = getBounds()

		context.fill(bounds.x - offset, bounds.y - offset, bounds.x + bounds.width + offset, bounds.y + bounds.height + offset, 0x80000000.toInt())
		context.drawBorder(bounds.x - offset - 1, bounds.y - offset - 1, bounds.width + 2 * offset + 2, bounds.height + 2 * offset + 2, color.toInt())

		val yOffset = if(scale < 1.0f) 10 else 0
		RenderUtils.drawCenteredText(context, name, bounds.x + bounds.width / 2, bounds.y + bounds.height / 2 - 4 - yOffset)
	}

	open fun moveTo(x: Int, y: Int) {
		val offset = scaleOffset + 1
		val (xMax, yMax) = maxScreenBounds

		this.x = x.coerceIn(offset, xMax)
		this.y = y.coerceIn(offset, yMax)
	}

	open fun moveBy(dx: Int = 0, dy: Int = 0) = moveTo(x + dx, y + dy)

	open fun adjustScale(delta: Float) {
		scale = (scale + delta).roundTo(1).coerceIn(minScale, maxScale)
	}

	open fun reset() {
		scale = 1.0f
		moveTo(0, 0)
	}
}