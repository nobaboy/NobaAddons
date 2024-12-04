package me.nobaboy.nobaaddons.screens.hud.elements

import me.nobaboy.nobaaddons.screens.NobaHudScreen
import me.nobaboy.nobaaddons.screens.hud.elements.data.Element
import me.nobaboy.nobaaddons.screens.hud.elements.data.ElementBounds
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import kotlin.math.roundToInt

abstract class HudElement(protected val element: Element) {
	val identifier get() = element.identifier
	var x: Int by element::x
	var y: Int by element::y
	var scale: Float by element::scale
	var color: Int by element::color

	open val minScale: Float = 0.5f
	open val maxScale: Float = 3.0f

	val shouldRender: Boolean
		get() {
			val client = MCUtils.client
			return enabled && client.currentScreen !is NobaHudScreen && !client.options.hudHidden
		}

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

	fun renderBackground(context: DrawContext, hovered: Boolean) {
		val color = if(hovered) 0xFF85858A else 0xFF343738

		val offset = (1 * scale).roundToInt().coerceAtLeast(1)
		val bounds = getBounds()

		context.fill(bounds.x - offset, bounds.y - offset, bounds.x + bounds.width + offset, bounds.y + bounds.height + offset, 0x80000000.toInt())
		context.drawBorder(bounds.x - offset - 1, bounds.y - offset - 1, bounds.width + 2 * offset + 2, bounds.height + 2 * offset + 2, color.toInt())

		val yOffset = if(scale < 1.0f) 10 else 0
		RenderUtils.drawCenteredText(context, identifier, bounds.x + bounds.width / 2, bounds.y + bounds.height / 2 - 4 - yOffset)
	}

	fun moveTo(x: Int, y: Int) {
		val offset = scaleOffset + 1
		val (xMax, yMax) = maxScreenBounds

		this.x = x.coerceIn(offset, xMax)
		this.y = y.coerceIn(offset, yMax)
	}

	fun moveBy(dx: Int = 0, dy: Int = 0) = moveTo(x + dx, y + dy)

	fun adjustScale(delta: Float) {
		scale = (scale + delta).roundTo(1).coerceIn(minScale, maxScale)
	}

	fun reset() {
		scale = 1.0f
		moveTo(0, 0)
	}
}