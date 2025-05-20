package me.nobaboy.nobaaddons.screens.keybinds

import me.nobaboy.nobaaddons.features.keybinds.impl.KeyBind
import me.nobaboy.nobaaddons.utils.ScreenUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.drawCenteredText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.screen.ScreenTexts
import org.lwjgl.glfw.GLFW

// TODO abstract this
class KeyBindsScreen(private val parent: Screen?) : Screen(tr("nobaaddons.screen.keybinds", "Key Binds")) {
	private lateinit var keyBindsList: KeyBindsListWidget
	private var initialized = false

	var selectedKeyBind: KeyBind? = null

	private val cancelButton = ButtonWidget.builder(ScreenTexts.CANCEL) { close() }.build()
	private val addButton = ButtonWidget.builder(tr("nobaaddons.screen.button.newKeybind", "New Key Bind")) { keyBindsList.create() }.build()
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
		context.drawCenteredText(title, width / 2, 12)
	}

	override fun close() {
		if(!keyBindsList.hasChanges) {
			client!!.setScreen(parent)
			return
		}

		ScreenUtils.confirmClose(this) {
			client!!.setScreen(parent)
			initialized = false
		}
	}
}