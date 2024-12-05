package me.nobaboy.nobaaddons.screens.infoboxes

import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

private val TITLE = Text.translatable("nobaaddons.screen.infoBoxesConfig")

class InfoBoxesConfigScreen(private val parent: Screen?) : Screen(TITLE) {
	private lateinit var infoBoxesListWidget: InfoBoxesListWidget

	override fun init() {
		super.init()

		infoBoxesListWidget = InfoBoxesListWidget(client!!, width, height - 96, 33, 32)
		addDrawableChild(infoBoxesListWidget)

		val gridWidget = GridWidget()
		gridWidget.mainPositioner.marginX(5).marginY(2)
		val adder = gridWidget.createAdder(3)

		adder.add(ButtonWidget.builder(Text.translatable("nobaaddons.screen.button.new", "Info Box")) { infoBoxesListWidget.addInfoBox() }.build())
		adder.add(ButtonWidget.builder(ScreenTexts.DONE) { close() }.build())

		gridWidget.refreshPositions()
		SimplePositioningWidget.setPos(gridWidget, 0, height - 64, width, 64)
		gridWidget.forEachChild { addDrawableChild(it) }
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)
		RenderUtils.drawCenteredText(context, this.title, this.width / 2, 12)
	}

	override fun close() {
		infoBoxesListWidget.saveChanges()
		client!!.setScreen(parent)
	}
}