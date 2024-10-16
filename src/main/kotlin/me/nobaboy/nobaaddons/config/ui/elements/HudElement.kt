package me.nobaboy.nobaaddons.config.ui.elements

import me.nobaboy.nobaaddons.config.ui.NobaHudScreen
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.round
import net.minecraft.client.gui.DrawContext

abstract class HudElement(element: Element) {
	val identifier: String by element::identifier
	var x: Int by element::x
	var y: Int by element::y
	var scale: Double by element::scale
	var color: Int by element::color

	abstract fun render(context: DrawContext, forced: Boolean, hovered: Boolean)
	abstract fun getBounds(): ElementBounds

	fun shouldRender(forced: Boolean): Boolean {
		val client = MCUtils.client
		return forced || (client.currentScreen !is NobaHudScreen && !client.options.hudHidden)
	}

	fun drawBackground(context: DrawContext, color: Int) {
		val bounds = getBounds()
		val offset = (1 * bounds.scale).toInt().coerceAtLeast(1)
		context.fill(
			bounds.x - offset,
			bounds.y - offset,
			bounds.x + bounds.width.toInt() + offset,
			bounds.y + bounds.height.toInt(),
			color
		)
	}

	fun modifyPosition(newX: Int, newY: Int) {
		val (width, height) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }
		val bounds = getBounds()

		val maxX = (width - bounds.width).coerceAtLeast(1)
		val maxY = (height - bounds.height).coerceAtLeast(1)

		x = newX.coerceIn(1, maxX)
		y = newY.coerceIn(1, maxY)
	}

	fun modifyScale(newScale: Double) {
		scale = (scale + newScale).round(1).coerceIn(MIN_SCALE, MAX_SCALE)
	}

	// Figure out a better way for default x and y
	fun reset() {
		scale = 1.0
	}

	companion object {
		const val MIN_SCALE = 0.5
		const val MAX_SCALE = 3.0

		const val NOT_HOVERED = 855638015
		const val HOVERED = -1761607681
	}
}