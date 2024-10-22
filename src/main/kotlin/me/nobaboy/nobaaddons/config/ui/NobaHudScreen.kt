package me.nobaboy.nobaaddons.config.ui

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.ui.elements.HudElement
import me.nobaboy.nobaaddons.features.ui.ElementManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ScreenUtils.queueOpen
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

class NobaHudScreen(parent: Screen) : Screen(TITLE) {
	companion object {
		private val TITLE = Text.translatable("nobaaddons.name")

		private const val BUTTON_WIDTH = 200
		private const val BUTTON_WIDTH_HALF = 96
	}

	private var parent: Screen? = null

	private lateinit var elements: LinkedHashMap<String, HudElement>

	private lateinit var doneButtonWidget: ButtonWidget
	private lateinit var setButtonWidget: ButtonWidget
	private lateinit var xPositionWidget: TextFieldWidget
	private lateinit var yPositionWidget: TextFieldWidget

	private var selectedElement: HudElement? = null
	private var hoveredElement: HudElement? = null
	private var keyboardElement: HudElement? = null

	private var editingMode: EditingMode? = null

	private var offsetX = 0f
	private var offsetY = 0f

	private var showUsageText: Boolean = NobaConfigManager.get().uiAndVisuals.showUsageText
	// TODO: Add usage text after context menu implementation
	private val usageTexts = arrayListOf<String>("usage text")

	init {
		this.parent = parent
	}

	override fun init() {
		val client = MCUtils.client
		val centerX = client.window.scaledWidth / 2
		val scaledHeight = client.window.scaledHeight

		elements = LinkedHashMap(ElementManager)
		showUsageText = NobaConfigManager.get().uiAndVisuals.showUsageText

		doneButtonWidget = ButtonWidget.builder(ScreenTexts.DONE) {
			this.close()
		}.dimensions(centerX - (BUTTON_WIDTH / 2), scaledHeight - 30, BUTTON_WIDTH, 20).build()

		setButtonWidget = ButtonWidget.builder(ScreenTexts.OK) {
			setEditingMode(EditingMode.IDLE)
		}.dimensions(centerX - (BUTTON_WIDTH_HALF / 2), scaledHeight - 220, BUTTON_WIDTH_HALF, 20).build()

		xPositionWidget = TextFieldWidget(
			client.textRenderer, centerX - BUTTON_WIDTH_HALF - 5, scaledHeight - 250, BUTTON_WIDTH_HALF, 20, Text.literal("")
		).also { widget ->
			widget.setChangedListener { input ->
				input.toIntOrNull()?.let { newX -> selectedElement?.let { it.modifyPosition(newX, it.y) } }
			}
		}

		yPositionWidget = TextFieldWidget(
			client.textRenderer, centerX + 5, scaledHeight - 250, BUTTON_WIDTH_HALF, 20, Text.literal("")
		).also { widget ->
			widget.setChangedListener { input ->
				input.toIntOrNull()?.let { newY -> selectedElement?.let { it.modifyPosition(it.x, newY) } }
			}
		}

		setEditingMode(EditingMode.IDLE)
		addDrawableChild(doneButtonWidget)
		addDrawableChild(setButtonWidget)
		addDrawableChild(xPositionWidget)
		addDrawableChild(yPositionWidget)
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)

		val window = MCUtils.window
		val centerX = window.scaledWidth / 2
		val scaledHeight = window.scaledHeight

		when(editingMode) {
			EditingMode.IDLE -> {
				RenderUtils.drawCenteredText(context, "Left Alt » Toggles usage text", centerX, scaledHeight - 40)
				usageTexts.takeIf { showUsageText }?.forEachIndexed { i, text ->
					RenderUtils.drawCenteredText(context, text, centerX, 5 + i * 10)
				}
			}

			EditingMode.EXACT -> {
				RenderUtils.drawCenteredText(context, "X:", centerX - (BUTTON_WIDTH_HALF / 2) - 4, scaledHeight - 260)
				RenderUtils.drawCenteredText(context, "Y:", centerX + (BUTTON_WIDTH_HALF / 2) + 4, scaledHeight - 260)
			}

			else -> {}
		}

		var hovered: HudElement? = null
		for(element in elements.values) {
			val isHovered = clickInBounds(element, mouseX.toDouble(), mouseY.toDouble())
			element.render(context, true, isHovered)
			if(isHovered) {
				hovered = element
				if(hasShiftDown()) {
					setTooltip(Tooltip.of(Text.literal(element.identifier)), HoveredTooltipPositioner.INSTANCE, false)
				}
			}
		}

		hoveredElement = hovered
		if(hovered == null) clearTooltip()
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		elements.values.forEach { element ->
			if(!clickInBounds(element, mouseX, mouseY)) return@forEach

			when(button) {
				GLFW.GLFW_MOUSE_BUTTON_1 -> {
					if(editingMode == EditingMode.EXACT) return true

					selectedElement = element
					val mode = if(hasControlDown()) EditingMode.EXACT else EditingMode.DRAG
					if(mode == EditingMode.DRAG) updateOffset(element, mouseX, mouseY)
					setEditingMode(mode)
				}

				GLFW.GLFW_MOUSE_BUTTON_3 -> element.reset()
			}
		}

		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		return when(editingMode) {
			EditingMode.EXACT -> true
			EditingMode.DRAG -> {
				setEditingMode(EditingMode.IDLE)
				selectedElement = null
				true
			}
			else -> super.mouseReleased(mouseX, mouseY, button)
		}
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
		selectedElement.takeIf { editingMode == EditingMode.DRAG }?.apply {
			selectedElement?.modifyPosition((mouseX - offsetX).toInt(), (mouseY - offsetY).toInt())
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
		hoveredElement.takeIf { editingMode != EditingMode.EXACT }.apply {
			this?.modifyScale((verticalAmount / 10))
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if(hoveredElement != null) keyboardElement = hoveredElement

		if(keyCode == GLFW.GLFW_KEY_LEFT_ALT) showUsageText = !showUsageText

		keyboardElement?.takeIf {
			editingMode != EditingMode.EXACT
		}?.let {
			when(keyCode) {
				GLFW.GLFW_KEY_UP -> it.y -= getMovementAmount()
				GLFW.GLFW_KEY_DOWN -> it.y += getMovementAmount()
				GLFW.GLFW_KEY_LEFT -> it.x -= getMovementAmount()
				GLFW.GLFW_KEY_RIGHT -> it.x += getMovementAmount()
				GLFW.GLFW_KEY_EQUAL -> it.modifyScale(0.1)
				GLFW.GLFW_KEY_MINUS -> it.modifyScale(-0.1)
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun close() {
		NobaConfigManager.save()
		parent?.queueOpen()
	}

	private fun updateOffset(element: HudElement, mouseX: Double, mouseY: Double) {
		val bounds = element.getBounds()
		offsetX = (mouseX - bounds.x).toFloat()
		offsetY = (mouseY - bounds.y).toFloat()
	}

	private fun setEditingMode(newMode: EditingMode) {
		editingMode = newMode
		val isIdle = editingMode == EditingMode.IDLE
		val isExact = editingMode == EditingMode.EXACT

		doneButtonWidget.visible = isIdle
		setButtonWidget.visible = isExact
		xPositionWidget.let {
			it.visible = isExact
			it.text = selectedElement?.x.toString()
		}
		yPositionWidget.let {
			it.visible = isExact
			it.text = selectedElement?.y.toString()
		}
	}

	private fun getMovementAmount(): Int = if(hasControlDown()) 10 else 1
	private fun clickInBounds(element: HudElement, mouseX: Double, mouseY: Double): Boolean {
		val bounds = element.getBounds()
		return RenderUtils.isPointInArea(
			mouseX, mouseY,
			bounds.x.toDouble(), bounds.y.toDouble(),
			(bounds.x + bounds.width).toDouble(), (bounds.y + bounds.height).toDouble()
		)
	}

	private enum class EditingMode {
		IDLE,
		DRAG,
		EXACT;
	}
}