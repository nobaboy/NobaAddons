package me.nobaboy.nobaaddons.screens.infoboxes

import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

private val TITLE = Text.translatable("nobaaddons.screen.infoBoxes")

class InfoBoxesScreen(private val parent: Screen?) : Screen(TITLE) {
	private lateinit var infoBoxesList: InfoBoxesListWidget
	private var initialized = false

	private val cancelButton = ButtonWidget.builder(ScreenTexts.CANCEL) { close() }.build()
	val addButton = ButtonWidget.builder(Text.translatable("nobaaddons.screen.button.new", "Key Bind")) { infoBoxesList.addInfoBox() }.build()
	private val doneButton = ButtonWidget.builder(ScreenTexts.DONE) {
		infoBoxesList.saveChanges()
		close()
	}.build()

	override fun init() {
		super.init()

		if(!initialized) {
			infoBoxesList = InfoBoxesListWidget(client!!, this, width, height - 96, 33, 32)
			initialized = true
		}

		infoBoxesList.setDimensions(width, height - 96)
		infoBoxesList.update()

		addDrawableChild(infoBoxesList)

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

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)
		RenderUtils.drawCenteredText(context, this.title.copy().append(" (${infoBoxesList.size}/20)"), this.width / 2, 12)
	}

	override fun close() {
		if(infoBoxesList.hasChanges) {
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