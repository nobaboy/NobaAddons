package me.nobaboy.nobaaddons.config.ui.elements

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.ui.NobaHudScreen
import net.minecraft.client.gui.DrawContext
import kotlin.math.roundToInt

abstract class HudElement(
	val identifier: String,
	var x: Int,
	var y: Int,
	var scale: Double = 1.0,
	var color: Int = 0xFFFFFF
) {
	abstract fun render(context: DrawContext, withBackground: Boolean, hovered: Boolean)
	abstract fun getName(): String
	abstract fun getBounds(): ElementBounds

	fun shouldRender(force: Boolean): Boolean {
		val client = NobaAddons.mc
		return force || (client.currentScreen !is NobaHudScreen && !client.options.hudHidden)
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
		val client = NobaAddons.mc
		val bounds = getBounds()
		val maxX = (client.window.scaledWidth - bounds.width)
		val maxY = (client.window.scaledHeight - bounds.height)

		if(bounds.width < maxX) x = newX.coerceIn(1, maxX)
		if(bounds.height < maxY) y = newY.coerceIn(1, maxY)
	}

	fun modifyScale(newScale: Double) {
		scale = ((scale + newScale).coerceIn(MIN_SCALE, MAX_SCALE) * 10).roundToInt() / 10.0
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