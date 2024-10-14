package me.nobaboy.nobaaddons.config.controllers.infobox

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.LowProfileButtonWidget
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.config.controllers.ControllerHelper
import me.nobaboy.nobaaddons.config.controllers.ControllerWidgetHelper
import me.nobaboy.nobaaddons.config.ui.ElementManager
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import me.nobaboy.nobaaddons.mixin.CategoryTabAccessor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.text.Text

class InfoBoxController(
	option: Option<InfoBox>,
	textControllerBuilder: (Option<String>) -> ControllerBuilder<String>,
	xControllerBuilder: (Option<Int>) -> ControllerBuilder<Int>,
	yControllerBuilder: (Option<Int>) -> ControllerBuilder<Int>,
	scaleControllerBuilder: (Option<Double>) -> ControllerBuilder<Double>,
) : ControllerHelper<InfoBox>(option) {
	private val textController: Controller<String> = createOption("Text:", textControllerBuilder,
		{ option.pendingValue().text },
		{ (ElementManager[option.pendingValue().identifier] as InfoBoxHud).infoBox.text = it }
	).controller()

	private val xController: Controller<Int> = createOption("X:", xControllerBuilder,
		{ option.pendingValue().x },
		{ (ElementManager[option.pendingValue().identifier] as InfoBoxHud)
				.modifyPosition(it, option.pendingValue().y) }
	).controller()

	private val yController: Controller<Int> = createOption("Y:", yControllerBuilder,
		{ option.pendingValue().y },
		{ (ElementManager[option.pendingValue().identifier] as InfoBoxHud)
				.modifyPosition(option.pendingValue().x, it) }
	).controller()

	private val scaleController: Controller<Double> = createOption("Scale:", scaleControllerBuilder,
		{ option.pendingValue().scale },
		{ (ElementManager[option.pendingValue().identifier] as InfoBoxHud).modifyScale(it) }
	).controller()

	private var collapsed: Boolean = true
	private val collapsedText get() =
		Text.literal(if(collapsed) "▶"  else "▼")

	fun setCollapsed(collapsed: Boolean) {
		this.collapsed = collapsed
	}

	override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
		val height = if(collapsed) COLLAPSED_HEIGHT else EXPANDED_HEIGHT
		val updatedWidgetDimension = widgetDimension.withHeight(height)

		val textWidget = textController.provideWidget(screen, updatedWidgetDimension.moved(0, 22))
		val xWidget = xController.provideWidget(screen, updatedWidgetDimension.moved(0, 44))
		val yWidget = yController.provideWidget(screen, updatedWidgetDimension.moved(0, 66))
		val scaleWidget = scaleController.provideWidget(screen, updatedWidgetDimension.moved(0, 88))

		return ControllerElement(this, screen, updatedWidgetDimension, textWidget, xWidget, yWidget, scaleWidget)
	}

	class Builder(private val option: Option<InfoBox>) : ControllerBuilder<InfoBox> {
		private val textControllerBuilder: (Option<String>) -> ControllerBuilder<String> = StringControllerBuilder::create
		private val xControllerBuilder: (Option<Int>) -> ControllerBuilder<Int> = IntegerFieldControllerBuilder::create
		private val yControllerBuilder: (Option<Int>) -> ControllerBuilder<Int> = IntegerFieldControllerBuilder::create
		private val scaleControllerBuilder: (Option<Double>) -> ControllerBuilder<Double> = DoubleFieldControllerBuilder::create

		companion object {
			fun create(option: Option<InfoBox>): Builder = Builder(option)
		}

		@Suppress("UnstableApiUsage")
		override fun build(): Controller<InfoBox> = InfoBoxController(
			option,
			textControllerBuilder,
			xControllerBuilder,
			yControllerBuilder,
			scaleControllerBuilder
		)
	}

	class ControllerElement(
		control: InfoBoxController,
		screen: YACLScreen,
		widgetDimension: Dimension<Int>,
		val textWidget: AbstractWidget,
		val xWidget: AbstractWidget,
		val yWidget: AbstractWidget,
		val scaleWidget: AbstractWidget
	) : ControllerWidgetHelper<InfoBoxController>(control, screen, widgetDimension) {
		val collapseWidget: LowProfileButtonWidget = LowProfileButtonWidget(
			widgetDimension.x(), widgetDimension.y(), 20, 20, control.collapsedText
		) { buttonWidget ->
			control.setCollapsed(!control.collapsed)
			buttonWidget.message = control.collapsedText

			(screen.tabManager.currentTab as CategoryTabAccessor).optionList.list.refreshOptions()
		}

		override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
			collapseWidget.render(context, mouseX, mouseY, delta)

			if(!control.collapsed) {
				textWidget.render(context, mouseX, mouseY, delta)
				xWidget.render(context, mouseX, mouseY, delta)
				yWidget.render(context, mouseX, mouseY, delta)
				scaleWidget.render(context, mouseX, mouseY, delta)
			}
		}

		@Suppress("UsePropertyAccessSyntax")
		override fun setDimension(widgetDimension: Dimension<Int>) {
			var defaultWidgetDimensions: Dimension<Int>

			if(control.collapsed) {
				widgetDimension.withHeight(20)
				defaultWidgetDimensions = widgetDimension
			} else {
				widgetDimension.withHeight(110)
				defaultWidgetDimensions = widgetDimension
					.withHeight(20)
					.withWidth(widgetDimension.width() + 40)
					.withX(widgetDimension.x() - 20)
			}

			collapseWidget.x = widgetDimension.x()
			collapseWidget.y = widgetDimension.y()

			textWidget.dimension = defaultWidgetDimensions.moved(0, 22)
			xWidget.dimension = defaultWidgetDimensions.moved(0, 44)
			yWidget.dimension = defaultWidgetDimensions.moved(0, 66)
			scaleWidget.dimension = defaultWidgetDimensions.moved(0, 88)

			super.setDimension(widgetDimension)
		}

		override fun guiWidgets(): List<Element> {
			return listOf(
				collapseWidget,
				textWidget,
				xWidget,
				yWidget,
				scaleWidget
			)
		}
	}

	companion object {
		private const val COLLAPSED_HEIGHT = 22
		private const val EXPANDED_HEIGHT = 110
	}
}