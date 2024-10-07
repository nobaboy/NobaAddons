package me.nobaboy.nobaaddons.screens

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.RenderUtils
import me.nobaboy.nobaaddons.utils.ScreenUtils.queueOpen
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

// TODO: Implement hud elements and finish this screen
class NobaHudScreen(parent: Screen) : Screen(TITLE) {
	companion object {
		private val TITLE = Text.translatable("nobaaddons.name")

		private const val BUTTON_WIDTH = 200
		private const val BUTTON_WIDTH_HALF = 96
	}

	private var parent: Screen? = null

	private var doneButtonWidget: ButtonWidget? = null
	private var setButtonWidget: ButtonWidget? = null
	private var xPositionWidget: TextFieldWidget? = null
	private var yPositionWidget: TextFieldWidget? = null

	private var editingMode: EditingMode? = null

	private var showUsageText: Boolean = true
	val usageTexts = arrayOf(
		"To select an element, Left Click it or hover over it",
		"Left Click » Drags an element",
		"Right Click » Resets an element's position",
		"Scroll or +/- » Changes the scale of an element",
		"Left Ctrl + Left Click (on an element) » Enables exact positioning",
		"Left Shift (hovering over an element) » Shows the element's name"
	)

	init {
		this.parent = parent
	}

	override fun init() {
		val client = NobaAddons.mc
		val centerX = client.window.scaledWidth / 2
		val scaledHeight = client.window.scaledHeight

		doneButtonWidget = ButtonWidget.builder(ScreenTexts.DONE) {
			parent?.queueOpen()
		}.dimensions(centerX - (BUTTON_WIDTH / 2), scaledHeight - 30, BUTTON_WIDTH, 20).build()

		setButtonWidget = ButtonWidget.builder(ScreenTexts.OK) {
			setEditingMode(EditingMode.IDLE)
		}.dimensions(centerX - (BUTTON_WIDTH_HALF / 2), scaledHeight - 220, BUTTON_WIDTH_HALF, 20).build()

		xPositionWidget = TextFieldWidget(
			client.textRenderer, centerX - BUTTON_WIDTH_HALF - 5, scaledHeight - 250, BUTTON_WIDTH_HALF, 20, Text.literal("")
		).also {
			it.setChangedListener { listener ->
				if(listener.isEmpty()) return@setChangedListener
				// set element x position
			}
		}

		yPositionWidget = TextFieldWidget(
			client.textRenderer, centerX + 5, scaledHeight - 250, BUTTON_WIDTH_HALF, 20, Text.literal("")
		).also {
			it.setChangedListener { listener ->
				if(listener.isEmpty()) return@setChangedListener
				// set element y position
			}
		}

		this.addDrawableChild(doneButtonWidget)
		this.addDrawableChild(setButtonWidget)
		this.addDrawableChild(xPositionWidget)
		this.addDrawableChild(yPositionWidget)
		setEditingMode(EditingMode.IDLE)
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		renderBackground(context, mouseX, mouseY, delta)
		super.render(context, mouseX, mouseY, delta)


		val client = NobaAddons.mc
		val centerX = client.window.scaledWidth / 2
		val scaledHeight = client.window.scaledHeight

		if(editingMode == EditingMode.IDLE) {
			RenderUtils.drawCenteredText(context, "Left Alt » Toggles usage text", centerX, scaledHeight - 40)
			if(showUsageText) {
				usageTexts.forEachIndexed { i, text ->
					RenderUtils.drawCenteredText(context, text, centerX, 5 + i * 10)
				}
			}
		} else if(editingMode == EditingMode.EXACT) {
			RenderUtils.drawCenteredText(context, "X:", centerX - (BUTTON_WIDTH_HALF / 2) - 4, scaledHeight - 260)
			RenderUtils.drawCenteredText(context, "Y:", centerX + (BUTTON_WIDTH_HALF / 2) + 4, scaledHeight - 260)
		}
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		val b = super.keyPressed(keyCode, scanCode, modifiers)

		if(keyCode == GLFW.GLFW_KEY_LEFT_ALT) showUsageText = !showUsageText
		return b
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		val b = super.mouseClicked(mouseX, mouseY, button)

		// check if the click is on a hud element

		when(button) {
			GLFW.GLFW_MOUSE_BUTTON_1 -> {
				if(hasControlDown() || editingMode == EditingMode.EXACT) {
					setEditingMode(EditingMode.EXACT)
					return b
				}

				setEditingMode(EditingMode.DRAG)
				// set selected element variable
			}

			GLFW.GLFW_MOUSE_BUTTON_2 -> {
				// reset selected element position
				return b
			}
		}
		return b
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
		val b = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
		if(editingMode != EditingMode.DRAG) return b
		// move selected element
		return b
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		val b = super.mouseReleased(mouseX, mouseY, button)
		if(this.editingMode != EditingMode.DRAG) return b

		setEditingMode(EditingMode.IDLE)
		// reset selected element variable
		return b
	}

	override fun close() {
		parent?.queueOpen()
	}

	private fun setEditingMode(editingMode: EditingMode) {
		this.editingMode = editingMode
		when(editingMode) {
			EditingMode.IDLE -> {
				doneButtonWidget?.visible = true
				setButtonWidget?.visible = false
				xPositionWidget?.visible = false
				yPositionWidget?.visible = false
			}

			EditingMode.DRAG -> {
				doneButtonWidget?.visible = false
				setButtonWidget?.visible = false
				xPositionWidget?.visible = false
				yPositionWidget?.visible = false
			}

			EditingMode.EXACT -> {
				doneButtonWidget?.visible = false
				setButtonWidget?.visible = true
				xPositionWidget?.visible = true
				yPositionWidget?.visible = true
			}
		}
	}


	private enum class EditingMode {
		IDLE,
		DRAG,
		EXACT;
	}
}