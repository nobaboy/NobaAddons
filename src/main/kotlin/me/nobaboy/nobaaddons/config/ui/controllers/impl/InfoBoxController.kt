package me.nobaboy.nobaaddons.config.ui.controllers.impl

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.LowProfileButtonWidget
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.ui.controllers.ControllerHelper
import me.nobaboy.nobaaddons.config.ui.controllers.ControllerWidgetHelper
import me.nobaboy.nobaaddons.config.ui.elements.TextElement
import me.nobaboy.nobaaddons.config.ui.elements.TextMode
import me.nobaboy.nobaaddons.mixins.accessors.CategoryTabAccessor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.text.Text

class InfoBoxController(
	option: Option<TextElement>,
	textControllerBuilder: (Option<String>) -> ControllerBuilder<String>,
	modeControllerBuilder: (Option<TextMode>) -> EnumControllerBuilder<TextMode>
) : ControllerHelper<TextElement>(option) {
	private val textController: Controller<String> = createOption<String>("Text:", textControllerBuilder,
		{ option.pendingValue().text },
		{ value ->
			val infoBox = option.pendingValue()
			option.requestSet(TextElement(value, infoBox.mode, infoBox.element))
		}
	).controller()
	private val modeController: Controller<TextMode> = createOption<TextMode>("Text Mode:", modeControllerBuilder,
		{ option.pendingValue().mode },
		{ value ->
			val infoBox = option.pendingValue()
			option.requestSet(TextElement(infoBox.text, value, infoBox.element))
		}
	).controller()

	private var collapsed: Boolean = true
	private val collapsedText
		get() = Text.literal(if(collapsed) "▶"  else "▼")

	fun setCollapsed(value: Boolean) {
		collapsed = value
	}

	override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
		val height = if(collapsed) COLLAPSED_HEIGHT else EXPANDED_HEIGHT
		val updatedWidgetDimension = widgetDimension.withHeight(height)

		val textWidget = textController.provideWidget(screen, updatedWidgetDimension.moved(0, 22))
		val modeWidget = modeController.provideWidget(screen, updatedWidgetDimension.moved(0, 44))

		return ControllerElement(this, screen, updatedWidgetDimension, textWidget, modeWidget)
	}

	class Builder(private val option: Option<TextElement>) : ControllerBuilder<TextElement> {
		private val textControllerBuilder: (Option<String>) -> ControllerBuilder<String> = StringControllerBuilder::create
		private val modeControllerBuilder: (Option<TextMode>) -> EnumControllerBuilder<TextMode> = NobaConfigUtils::createCyclingController

		companion object {
			fun create(option: Option<TextElement>): Builder = Builder(option)
		}

		override fun build(): Controller<TextElement> = InfoBoxController(option, textControllerBuilder, modeControllerBuilder)
	}

	class ControllerElement(
		control: InfoBoxController,
		screen: YACLScreen,
		widgetDimension: Dimension<Int>,
		private val textWidget: AbstractWidget,
		private val modeWidget: AbstractWidget
	) : ControllerWidgetHelper<InfoBoxController>(control, screen, widgetDimension) {
		private val collapseWidget = LowProfileButtonWidget(
			widgetDimension.x(), widgetDimension.y(), 20, 20, control.collapsedText
		) { buttonWidget ->
			control.setCollapsed(!control.collapsed)
			buttonWidget.message = control.collapsedText

			(screen.tabManager.currentTab as CategoryTabAccessor).optionList.list.refreshOptions()
		}

		override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
			collapseWidget.render(context, mouseX, mouseY, delta)

			if(control.collapsed) return
			textWidget.render(context, mouseX, mouseY, delta)
			modeWidget.render(context, mouseX, mouseY, delta)
		}

		override fun setDimension(widgetDimension: Dimension<Int>) {
			var defaultWidgetDimensions: Dimension<Int>

			if(control.collapsed) {
				widgetDimension.withHeight(22)
				defaultWidgetDimensions = widgetDimension
			} else {
				widgetDimension.withHeight(66)
				defaultWidgetDimensions = widgetDimension
					.withHeight(20)
					.withWidth(widgetDimension.width() + 40)
					.withX(widgetDimension.x() - 20)
			}

			collapseWidget.x = widgetDimension.x()
			collapseWidget.y = widgetDimension.y()

			textWidget.dimension = defaultWidgetDimensions.moved(0, 22)
			modeWidget.dimension = defaultWidgetDimensions.moved(0, 44)

			super.setDimension(widgetDimension)
		}

		override fun guiElements(): List<Element> = listOf(collapseWidget, textWidget, modeWidget)
	}

	companion object {
		private const val COLLAPSED_HEIGHT = 22
		private const val EXPANDED_HEIGHT = 66
	}
}