package me.nobaboy.nobaaddons.config.ui

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.drawCentered
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ConfirmLinkScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

class NobaMainScreen : Screen(TITLE) {
	private var layout: ThreePartsLayoutWidget? = null

	companion object {
		private val TITLE = Text.translatable("nobaaddons.name")

		private const val SPACING = 8
		private const val BUTTON_WIDTH = 200
		private const val BUTTON_WIDTH_HALF = 96

		private const val GITHUB_ROOT = "https://github.com/nobaboy/NobaAddons"

		private val VERSION = "v${NobaAddons.VERSION}"
		private val CONFIGURATION_TEXT = Text.translatable("nobaaddons.config.open")
		private val EDIT_LOCATIONS_TEXT = Text.translatable("nobaaddons.config.edit")
		private val SOURCE_TEXT = Text.translatable("nobaaddons.config.github")
		private val ISSUES_TEXT = Text.translatable("nobaaddons.config.issues")
		private val MODRINTH_TEXT = Text.translatable("nobaaddons.config.modrinth")
		private val LEGAL_TEXT = Text.translatable("nobaaddons.config.legal")
	}

	override fun init() {
		layout = ThreePartsLayoutWidget(this, 150, 100)

		val gridWidget = layout!!.addBody(GridWidget()).setSpacing(SPACING)
		gridWidget.mainPositioner.alignHorizontalCenter()
		val adder = gridWidget.createAdder(2)

		adder.add(ButtonWidget.builder(CONFIGURATION_TEXT) { openConfig() }.width(BUTTON_WIDTH).build(), 2)
		if(MCUtils.world != null) adder.add(ButtonWidget.builder(EDIT_LOCATIONS_TEXT) { openHudEditor() }.width(BUTTON_WIDTH).build(), 2)
		adder.add(ButtonWidget.builder(SOURCE_TEXT, ConfirmLinkScreen.opening(this, GITHUB_ROOT)).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(ISSUES_TEXT, ConfirmLinkScreen.opening(this, "$GITHUB_ROOT/issues")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(MODRINTH_TEXT, ConfirmLinkScreen.opening(this, "https://modrinth.com/mod/nobaaddons")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(LEGAL_TEXT, ConfirmLinkScreen.opening(this, "$GITHUB_ROOT/LICENSE")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(ScreenTexts.DONE) { close() }.width(BUTTON_WIDTH).build(), 2)

		layout!!.refreshPositions()
		layout!!.forEachChild { addDrawableChild(it) }
	}

	override fun initTabNavigation() {
		super.initTabNavigation()
		layout?.refreshPositions()
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		renderBackground(context, mouseX, mouseY, delta)
		super.render(context, mouseX, mouseY, delta)

		val centerX = MCUtils.window.scaledWidth / 2
		TITLE.drawCentered(context, centerX, 75, 4.0f, 0x007AFF, true)
		VERSION.drawCentered(context, centerX, 107, 1.5f, 0xFFFFFF, true)
	}

	override fun close() {
		ElementManager.loadElements()
		super.close()
	}

	private fun openConfig() {
		client?.setScreen(NobaConfigManager.getConfigScreen(this))
	}

	private fun openHudEditor() {
		client?.setScreen(NobaHudScreen(this))
	}
}