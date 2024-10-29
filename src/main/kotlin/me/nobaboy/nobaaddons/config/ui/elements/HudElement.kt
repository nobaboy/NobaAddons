package me.nobaboy.nobaaddons.config.ui.elements

import me.nobaboy.nobaaddons.config.ui.NobaHudScreen
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.round
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import kotlin.math.roundToInt

abstract class HudElement(protected val element: Element) {
	val identifier get() = element.identifier
	var x: Int by element::x
	var y: Int by element::y
	var scale: Double by element::scale
	var color: Int by element::color

	open val minScale: Double = 0.5
	open val maxScale: Double = 3.0

	abstract fun render(context: DrawContext)
	abstract fun getBounds(): ElementBounds

	val shouldRender: Boolean
		get() {
			val client = MCUtils.client
			return client.currentScreen !is NobaHudScreen && !client.options.hudHidden
		}

	fun renderBackground(context: DrawContext, hovered: Boolean) {
		val color = if(hovered) -1761607681 else 855638015
		val bounds = getBounds()
		val offset = (1 * scale).roundToInt().coerceAtLeast(1)

		context.fill(
			bounds.x - offset,
			bounds.y - offset,
			bounds.x + bounds.width + offset,
			bounds.y + bounds.height + offset,
			color
		)
		RenderUtils.drawCenteredText(context, identifier, bounds.x + bounds.width / 2, bounds.y + bounds.height / 2 - 4)
	}

	private fun calculateBounds(): Pair<Int, Int> {
		val (screenWidth, screenHeight) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }
		val bounds = getBounds()
		val offset = (1 * scale).roundToInt().coerceAtLeast(1)

		val maxX = if (bounds.width >= screenWidth) screenWidth - offset else (screenWidth - bounds.width).coerceAtLeast(1) - offset
		val maxY = if (bounds.height >= screenHeight) screenHeight - offset else (screenHeight - bounds.height).coerceAtLeast(1) - offset

		return maxX to maxY
	}

	fun moveTo(newX: Int, newY: Int) {
		val offset = (1 * scale).roundToInt().coerceAtLeast(1)
		val (maxX, maxY) = calculateBounds()

		x = if (maxX < offset) offset else newX.coerceIn(offset, maxX)
		y = if (maxY < offset) offset else newY.coerceIn(offset, maxY)
	}

	fun moveBy(xOffset: Int = 0, yOffset: Int = 0) {
		moveTo(x + xOffset, y + yOffset)
	}

	fun adjustScale(delta: Double) {
		scale = (scale + delta).round(1).coerceIn(minScale, maxScale)
	}

	fun reset() {
		x = 1
		y = 1
		scale = 1.0
	}
}