package me.nobaboy.nobaaddons.screens

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.screens.hud.ElementManager
import me.nobaboy.nobaaddons.screens.hud.elements.HudElement
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import kotlin.math.roundToInt

private val TITLE = Text.translatable("nobaaddons.screen.hudEditor")
private const val BUTTON_WIDTH = 200

class NobaHudScreen(private val parent: Screen?) : Screen(TITLE) {
	private lateinit var elements: LinkedHashMap<String, HudElement>
	private lateinit var doneButtonWidget: ButtonWidget
//	private var contextMenu: ContextMenu? = null

	private var editingMode: EditingMode = EditingMode.IDLE

	private val movementAmount: Int get() = if(hasControlDown()) 10 else 1
	private var offsetX = 0f
	private var offsetY = 0f

	private var selectedElement: HudElement? = null
	private var hoveredElement: HudElement? = null

	private var showUsageText: Boolean = NobaConfigManager.config.uiAndVisuals.showUsageText
	private val usageTexts = listOf<String>(
		"Left click and drag or use arrow keys to move (holding Ctrl moves further)",
		"Scroll or use +/- to rescale an element",
		"Middle click to reset an element",
		"Right click on an element to open context menu (soonTM)"
	)

	override fun init() {
		super.init()

		val window = MCUtils.window

		elements = LinkedHashMap(ElementManager)
		doneButtonWidget = ButtonWidget.builder(ScreenTexts.DONE) {
			this.close()
		}.dimensions(window.scaledWidth / 2 - BUTTON_WIDTH / 2, window.scaledHeight - 30, BUTTON_WIDTH, 20).build()

		addDrawableChild(doneButtonWidget)
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)
		render(context, mouseX, mouseY)
	}

	private fun render(context: DrawContext, mouseX: Int, mouseY: Int) {
		val (scaledWidth, scaledHeight) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }
		when(editingMode) {
			EditingMode.IDLE -> renderIdleText(context, scaledWidth, scaledHeight)
			else -> Unit
		}

		var hovered: HudElement? = null
		elements.values.forEach { element ->
			val isHovered = clickInBounds(element, mouseX.toDouble(), mouseY.toDouble())
			element.renderBackground(context, isHovered)
			if(isHovered) hovered = element
		}

		hoveredElement = hovered
	}

	private fun renderIdleText(context: DrawContext, scaledWidth: Int, scaledHeight: Int) {
		RenderUtils.drawCenteredText(context, Text.literal("Press Left Alt to toggle usage text"), scaledWidth / 2, scaledHeight - 45)
		if(showUsageText) usageTexts.forEachIndexed { i, text ->
			RenderUtils.drawCenteredText(context, text, scaledWidth / 2, 5 + i * 10)
		}
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		elements.values.forEach { element ->
			if(!clickInBounds(element, mouseX, mouseY)) return@forEach

			when(button) {
				GLFW.GLFW_MOUSE_BUTTON_1 -> {
					selectedElement = element
					updateOffset(element, mouseX, mouseY)
					setEditingMode(EditingMode.DRAG)
				}
//				GLFW.GLFW_MOUSE_BUTTON_2 -> {
//					openContextMenu(element)
//					setEditingMode(EditingMode.MENU)
//				}
				GLFW.GLFW_MOUSE_BUTTON_3 -> element.reset()
			}
		}

		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if(editingMode == EditingMode.DRAG) {
			setEditingMode(EditingMode.IDLE)
			selectedElement = null
			return true
		}
		return super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
		selectedElement.takeIf { editingMode == EditingMode.DRAG }?.moveTo((mouseX - offsetX).toInt(), (mouseY - offsetY).toInt())
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
		hoveredElement.takeIf { editingMode != EditingMode.MENU }?.adjustScale((verticalAmount / 10).toFloat())
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		hoveredElement?.let { selectedElement = it }
		if(keyCode == GLFW.GLFW_KEY_LEFT_ALT) showUsageText = !showUsageText

		selectedElement?.takeIf {
			editingMode != EditingMode.MENU
		}?.let {
			when(keyCode) {
				GLFW.GLFW_KEY_UP -> it.moveBy(dy = -movementAmount)
				GLFW.GLFW_KEY_DOWN -> it.moveBy(dy = movementAmount)
				GLFW.GLFW_KEY_LEFT -> it.moveBy(dx = -movementAmount)
				GLFW.GLFW_KEY_RIGHT -> it.moveBy(dx = movementAmount)
				GLFW.GLFW_KEY_EQUAL -> it.adjustScale(0.1f)
				GLFW.GLFW_KEY_MINUS -> it.adjustScale(-0.1f)
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun close() {
		ElementManager.loadElements()
		NobaConfigManager.config.uiAndVisuals.showUsageText = showUsageText
		NobaConfigManager.save()
		client!!.setScreen(parent)
	}

	private fun updateOffset(element: HudElement, mouseX: Double, mouseY: Double) {
		val bounds = element.getBounds()
		offsetX = (mouseX - bounds.x).toFloat()
		offsetY = (mouseY - bounds.y).toFloat()
	}

	private fun clickInBounds(element: HudElement, mouseX: Double, mouseY: Double): Boolean {
		val bounds = element.getBounds()
		val offset = (1 * element.scale).roundToInt().coerceAtLeast(1)
		return RenderUtils.isPointInArea(
			mouseX,
			mouseY,
			bounds.x.toDouble() - offset,
			bounds.y.toDouble() - offset,
			(bounds.x + bounds.width).toDouble() + offset,
			(bounds.y + bounds.height).toDouble() + offset
		)
	}

	private fun setEditingMode(newMode: EditingMode) {
		editingMode = newMode
		val isIdle = editingMode == EditingMode.IDLE
//		val isMenu = editingMode == EditingMode.MENU

		doneButtonWidget.visible = isIdle
//		contextMenu?.visible = isMenu
	}

	private enum class EditingMode {
		IDLE,
		DRAG,
		MENU
	}
}