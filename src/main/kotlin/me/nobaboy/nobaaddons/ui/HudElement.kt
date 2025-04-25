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

/**
 * Abstract HUD element
 *
 * @see TextHudElement
 */
abstract class HudElement {
	protected abstract val elementPosition: ElementPosition

	/**
	 * Returns an absolute pixel value for the top left corner of this element.
	 */
	var x: Int
		get() = convertToAbsolutePixel(MCUtils.window.scaledWidth, elementPosition.x)
		set(value) { elementPosition.x = value.toDouble() / MCUtils.window.scaledWidth }

	/**
	 * Returns an absolute pixel value for the top left corner of this element.
	 */
	var y: Int
		get() = convertToAbsolutePixel(MCUtils.window.scaledHeight, elementPosition.y)
		set(value) { elementPosition.y = value.toDouble() / MCUtils.window.scaledHeight }

	/**
	 * Shorthand for:
	 *
	 * ```kt
	 * if(elementPosition.x > 0.5) ElementAlignment.RIGHT else ElementAlignment.LEFT
	 * ```
	 */
	open val alignment: ElementAlignment
		get() = if(elementPosition.x > 0.5) ElementAlignment.RIGHT else ElementAlignment.LEFT

	/**
	 * If `false`, this UI element is entirely skipped - both in rendering and HUD editor screen.
	 *
	 * This should ideally be the value of the relevant config option.
	 */
	open val enabled: Boolean = true

	private fun convertToAbsolutePixel(pixels: Int, scale: Double): Int = (pixels * scale).toInt().coerceIn(0, pixels)

	/**
	 * The current element scale as determined by the user.
	 *
	 * @see RenderUtils.scaled
	 */
	open var scale: Float
		get() = if(allowScaling) elementPosition.scale else 1f
		set(value) { if(allowScaling) elementPosition.scale = value }

	/**
	 * Minimum scale for this element when using the scroll wheel on it in the HUD editor screen.
	 *
	 * Note that this is capped at `0.5f` in [ElementPosition].
	 */
	open val minScale: Float = 0.5f

	/**
	 * Maximum scale for this element when using the scroll wheel on it in the HUD editor screen.
	 *
	 * Note that this is capped at `3f` in [ElementPosition].
	 */
	open val maxScale: Float = 3f

	open val allowScaling: Boolean = true
	open val dynamicScaling: Boolean = false

	/**
	 * Name used in place of this element in the HUD editor screen.
	 */
	abstract val name: Text

	/**
	 * How large this element is on screen in pixels.
	 */
	abstract val size: Pair<Int, Int>

	val scaledSize: Pair<Int, Int>
		get() {
			if(!allowScaling || dynamicScaling) return size
			val (width, height) = size
			return (width * scale).roundToInt() to (height * scale).roundToInt()
		}

	/**
	 * Implement this method with your element's rendering logic.
	 */
	abstract fun render(context: DrawContext)

	private val scaleOffset: Int get() = scale.roundToInt().coerceAtLeast(1)

	private val maxScreenBounds: Pair<Int, Int>
		get() {
			val (screenWidth, screenHeight) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }
			val offset = scaleOffset
			val bounds = getBounds()

			val xMax = (screenWidth - bounds.width - offset - 1).coerceAtLeast(1)
			val yMax = (screenHeight - bounds.height - offset - 1).coerceAtLeast(1)
			return xMax to yMax
		}

	/**
	 * Override this to control when this should render; you should still `&&` the result of `super.shouldRender()`
	 * with your additional checks.
	 */
	open fun shouldRender(): Boolean {
		val client = MCUtils.client
		return !client.options.hudHidden && client.currentScreen !is NobaHudScreen
	}

	/**
	 * Renders the background for this element with a border indicating if it's hovered or not.
	 *
	 * This is primarily used in the HUD editor screen.
	 */
	fun renderEditorBackground(context: DrawContext, hovered: Boolean, renderExample: Boolean = true) {
		val color = if(hovered) 0xFF85858A else 0xFF343738
		val offset = scaleOffset
		val bounds = getBounds()

		context.fill(
			bounds.x - offset,
			bounds.y - offset,
			bounds.x + bounds.width + offset,
			bounds.y + bounds.height + offset,
			0x80000000.toInt()
		)

		context.drawBorder(
			bounds.x - offset - 1,
			bounds.y - offset - 1,
			bounds.width + 2 * offset + 2,
			bounds.height + 2 * offset + 2,
			color.toInt()
		)

		if(renderExample) {
			val yOffset = if(scale < 1f) 10 else 0
			renderExampleText(context, bounds, yOffset)
		}
	}

	/**
	 * Renders the background for this element.
	 */
	fun renderBackground(context: DrawContext) {
		val offset = scaleOffset
		val bounds = getBounds()

		context.fill(
			bounds.x - offset,
			bounds.y - offset,
			bounds.x + bounds.width + offset,
			bounds.y + bounds.height + offset,
			0x80000000.toInt()
		)
	}

	/**
	 * Display example text in the UI; the default implementation of this method simply renders [name].
	 */
	protected open fun renderExampleText(context: DrawContext, bounds: ElementBounds, yOffset: Int) {
		RenderUtils.drawCenteredText(
			context,
			name,
			bounds.x + bounds.width / 2,
			bounds.y + bounds.height / 2 - 4 - yOffset
		)
	}

	/**
	 * Internal method, returns the element's [ElementBounds] used by the HUD editor screen.
	 */
	fun getBounds(): ElementBounds = ElementBounds(x, y, scaledSize)

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
		scale = 1f.coerceIn(minScale, maxScale)
		moveTo(0, 0)
	}
}