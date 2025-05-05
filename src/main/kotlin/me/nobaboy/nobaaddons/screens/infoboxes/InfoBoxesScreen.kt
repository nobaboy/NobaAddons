package me.nobaboy.nobaaddons.screens.infoboxes

import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxesManager
import me.nobaboy.nobaaddons.utils.mc.ScreenUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.screen.ScreenTexts

class InfoBoxesScreen(private val parent: Screen?) : Screen(tr("nobaaddons.screen.infoBoxes", "Info Boxes")) {
	private lateinit var infoBoxesList: InfoBoxesListWidget
	private var initialized = false

	private val cancelButton = ButtonWidget.builder(ScreenTexts.CANCEL) { close() }.build()
	internal val addButton = ButtonWidget.builder(tr("nobaaddons.screen.button.newInfoBox", "New Info Box")) { infoBoxesList.create() }.build()
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

	private fun actuallyClose() {
		InfoBoxesManager.recreateUIElements()
		client!!.setScreen(parent)
		initialized = false
	}

	override fun close() {
		if(!infoBoxesList.hasChanges) {
			actuallyClose()
			return
		}

		ScreenUtils.confirmClose(this, this::actuallyClose)
	}
}