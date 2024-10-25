package me.nobaboy.nobaaddons.config.ui.controllers

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.ControllerWidget
import net.minecraft.client.gui.Element
import org.lwjgl.glfw.GLFW

abstract class ControllerWidgetHelper<T : Controller<*>>(
	control: T,
	screen: YACLScreen,
	widgetDimension: Dimension<Int>
) : ControllerWidget<T>(control, screen, widgetDimension) {
	abstract fun guiElements(): List<Element>

	fun forElement(action: (Element) -> Unit) {
		guiElements().forEach(action)
	}

	fun anyElementMatches(action: (Element) -> Boolean): Boolean = guiElements().any(action)

	override fun getHoveredControlWidth(): Int = unhoveredControlWidth

	override fun mouseMoved(mouseX: Double, mouseY: Double) {
		forElement { it.mouseMoved(mouseX, mouseY) }
		super.mouseMoved(mouseX, mouseY)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		return anyElementMatches { it.mouseClicked(mouseX, mouseY, button) } ||
			super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		return anyElementMatches { it.mouseReleased(mouseX, mouseY, button) } ||
			super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
		return anyElementMatches { it.mouseDragged(mouseX, mouseY, button, dragX, dragY) } ||
			super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
		return anyElementMatches { it.mouseScrolled(mouseX, mouseY, scrollX, scrollY) } ||
			super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		return when(keyCode) {
			GLFW.GLFW_KEY_TAB, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_ENTER -> false
			else -> anyElementMatches { it.keyPressed(keyCode, scanCode, modifiers) } ||
				super.keyPressed(keyCode, scanCode, modifiers)
		}
	}

	override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		return anyElementMatches { it.keyReleased(keyCode, scanCode, modifiers) } ||
			super.keyReleased(keyCode, scanCode, modifiers)
	}

	override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
		return anyElementMatches { it.charTyped(codePoint, modifiers) } ||
			super.charTyped(codePoint, modifiers)
	}

	override fun isFocused(): Boolean = anyElementMatches { it.isFocused }

	override fun setFocused(focused: Boolean) {
		forElement { it.isFocused = focused }
	}
}

