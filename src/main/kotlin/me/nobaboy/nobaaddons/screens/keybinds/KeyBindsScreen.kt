package me.nobaboy.nobaaddons.screens.keybinds

import me.nobaboy.nobaaddons.screens.keybinds.impl.KeyBind
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

private val TITLE = Text.translatable("nobaaddons.screen.keyBinds")

class KeyBindsScreen(private val parent: Screen?) : Screen(TITLE) {
	private lateinit var keyBindsList: KeyBindsListWidget
	private var initialized = false

	var selectedKeyBind: KeyBind? = null

	private val cancelButton = ButtonWidget.builder(ScreenTexts.CANCEL) { close() }.build()
	val addButton = ButtonWidget.builder(Text.translatable("nobaaddons.screen.button.new", "Key Bind")) { keyBindsList.addKeyBind() }.build()
	private val doneButton = ButtonWidget.builder(ScreenTexts.DONE) {
		keyBindsList.saveChanges()
		close()
	}.build()

	override fun init() {
		super.init()

		if(!initialized) {
			keyBindsList = KeyBindsListWidget(client!!, this, width, height - 96, 33, 32)
			initialized = true
		}

		keyBindsList.setDimensions(width, height - 96)
		keyBindsList.update()

		addDrawableChild(keyBindsList)

		val gridWidget = GridWidget()
		gridWidget.mainPositioner.marginX(5).marginY(2)
		val adder = gridWidget.createAdder(3)

		adder.add(cancelButton)
		adder.add(addButton)
		adder.add(doneButton)

		gridWidget.refreshPositions()
		SimplePositioningWidget.setPos(gridWidget, 0, height - 64, width, 64)
		gridWidget.forEachChild(this::addDrawableChild)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if(selectedKeyBind != null) {
			val code = when(button) {
				GLFW.GLFW_MOUSE_BUTTON_1, GLFW.GLFW_MOUSE_BUTTON_2, GLFW.GLFW_MOUSE_BUTTON_3 -> return true
				else -> button
			}

			if(selectedKeyBind!!.key != code) keyBindsList.hasChanges = true
			selectedKeyBind!!.key = code
			selectedKeyBind = null
			keyBindsList.update()
			return true
		}

		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if(selectedKeyBind != null) {
			val code = when(keyCode) {
				GLFW.GLFW_KEY_ESCAPE -> GLFW.GLFW_KEY_UNKNOWN
				else -> keyCode
			}

			if(selectedKeyBind!!.key != code) keyBindsList.hasChanges = true
			selectedKeyBind!!.key = code
			selectedKeyBind = null
			keyBindsList.update()
			return true
		}

		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)
		RenderUtils.drawCenteredText(context, this.title, this.width / 2, 12)
	}

	override fun close() {
		if(keyBindsList.hasChanges) {
			client!!.setScreen(ConfirmScreen(
				{ confirmed ->
					if(confirmed) {
						client!!.setScreen(parent)
						initialized = false
					} else {
						client!!.setScreen(this)
					}
				},
				Text.translatable("nobaaddons.screen.confirm"),
				Text.translatable("nobaaddons.screen.confirm.message"),
				ScreenTexts.YES,
				ScreenTexts.NO
			))
		} else {
			client!!.setScreen(parent)
		}
	}
}