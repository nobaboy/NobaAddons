package me.nobaboy.nobaaddons.ui.elements

import me.nobaboy.nobaaddons.screens.NobaHudScreen
import me.nobaboy.nobaaddons.ui.elements.data.ElementPosition
import me.nobaboy.nobaaddons.ui.elements.data.ElementBounds
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.math.roundToInt

abstract class HudElement(protected val elementPosition: ElementPosition) {
	open var x: Int by elementPosition::x
	open var y: Int by elementPosition::y
	open var scale: Float by elementPosition::scale

	open val minScale: Float = 0.5f
	open val maxScale: Float = 3.0f

	abstract val name: Text
	abstract val enabled: Boolean
	abstract fun render(context: DrawContext)
	abstract fun getBounds(): ElementBounds

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
		return enabled && !client.options.hudHidden && client.currentScreen !is NobaHudScreen
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