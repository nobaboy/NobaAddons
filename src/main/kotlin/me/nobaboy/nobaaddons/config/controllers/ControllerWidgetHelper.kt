package me.nobaboy.nobaaddons.config.controllers

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.ControllerWidget
import net.minecraft.client.gui.Element

abstract class ControllerWidgetHelper<T : Controller<*>>(
	controller: T, screen: YACLScreen, widgetDimension: Dimension<Int>
): ControllerWidget<T>(controller, screen, widgetDimension) {

	abstract fun guiWidgets(): List<Element>

	fun forWidget(action: (Element) -> Unit) {
		guiWidgets().forEach(action)
	}

	fun anyWidgetMatches(action: (Element) -> Boolean): Boolean = guiWidgets().any(action)

	override fun getHoveredControlWidth(): Int = unhoveredControlWidth

	override fun mouseMoved(mouseX: Double, mouseY: Double) {
		forWidget { widget -> widget.mouseMoved(mouseX, mouseY) }
		super.mouseMoved(mouseX, mouseY)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		return anyWidgetMatches { widget -> widget.mouseClicked(mouseX, mouseY, button) } ||
			super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		return anyWidgetMatches { widget -> widget.mouseReleased(mouseX, mouseY, button) } ||
			super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
		return anyWidgetMatches { widget -> widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) } ||
			super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
		return anyWidgetMatches { widget -> widget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount) } ||
			super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
	}

	// Figure out a way to stop space and enter presses if an input field is selected
	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		return anyWidgetMatches { widget -> widget.keyPressed(keyCode, scanCode, modifiers) }
			|| super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		return anyWidgetMatches { widget -> widget.keyReleased(keyCode, scanCode, modifiers) }
			|| super.keyReleased(keyCode, scanCode, modifiers)
	}

	override fun charTyped(chr: Char, modifiers: Int): Boolean {
		return anyWidgetMatches { widget -> widget.charTyped(chr, modifiers) } ||
			super.charTyped(chr, modifiers)
	}

	override fun isFocused(): Boolean {
		return anyWidgetMatches(Element::isFocused)
	}

	override fun setFocused(focused: Boolean) {
		forWidget { widget -> widget.isFocused = focused }
	}
}