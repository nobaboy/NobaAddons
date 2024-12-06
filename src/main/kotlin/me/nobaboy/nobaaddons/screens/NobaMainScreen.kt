package me.nobaboy.nobaaddons.screens

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.screens.hud.ElementManager
import me.nobaboy.nobaaddons.screens.keybinds.KeyBindsScreen
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ConfirmLinkScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

private val TITLE = Text.translatable("nobaaddons.screen.main")

private const val SPACING = 8
private const val BUTTON_WIDTH = 200
private const val BUTTON_WIDTH_HALF = 96

class NobaMainScreen : Screen(TITLE) {
	private var layout: ThreePartsLayoutWidget = ThreePartsLayoutWidget(this, 150, 20)

	companion object {
		private const val GITHUB_ROOT = "https://github.com/nobaboy/NobaAddons"

		private val TITLE_TEXT = Text.translatable("nobaaddons.name")
		private val VERSION_TEXT = "v${NobaAddons.VERSION}"
		private val CONFIGURATION_TEXT = Text.translatable("nobaaddons.screen.main.button.config")
		private val EDIT_LOCATIONS_TEXT = Text.translatable("nobaaddons.screen.main.button.hud")
		private val EDIT_KEYBINDS_TEXT = Text.translatable("nobaaddons.screen.main.button.keyBinds")
		private val SOURCE_TEXT = Text.translatable("nobaaddons.screen.main.button.github")
		private val ISSUES_TEXT = Text.translatable("nobaaddons.screen.main.button.issues")
		private val MODRINTH_TEXT = Text.translatable("nobaaddons.screen.main.button.modrinth")
		private val LEGAL_TEXT = Text.translatable("nobaaddons.screen.main.button.legal")
	}

	override fun init() {
		val gridWidget = layout.addBody(GridWidget()).setSpacing(SPACING)
		gridWidget.mainPositioner.alignHorizontalCenter()
		val adder = gridWidget.createAdder(2)

		adder.add(ButtonWidget.builder(CONFIGURATION_TEXT) { openConfig() }.width(BUTTON_WIDTH).build(), 2)
		if(MCUtils.world != null) {
			adder.add(ButtonWidget.builder(EDIT_LOCATIONS_TEXT) { openHudEditor() }.width(BUTTON_WIDTH_HALF).build())
			adder.add(ButtonWidget.builder(EDIT_KEYBINDS_TEXT) { openKeybindsEditor() }.width(BUTTON_WIDTH_HALF).build())
		} else {
			adder.add(ButtonWidget.builder(EDIT_KEYBINDS_TEXT) { openKeybindsEditor() }.width(BUTTON_WIDTH).build(), 2)
		}
		adder.add(ButtonWidget.builder(SOURCE_TEXT, ConfirmLinkScreen.opening(this, GITHUB_ROOT)).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(ISSUES_TEXT, ConfirmLinkScreen.opening(this, "$GITHUB_ROOT/issues")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(MODRINTH_TEXT, ConfirmLinkScreen.opening(this, "https://modrinth.com/mod/nobaaddons")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(LEGAL_TEXT, ConfirmLinkScreen.opening(this, "$GITHUB_ROOT/blob/master/LICENSE")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(ScreenTexts.DONE) { close() }.width(BUTTON_WIDTH).build(), 2)

		layout.refreshPositions()
		layout.forEachChild { addDrawableChild(it) }
	}

	//? if >=1.21.2 {
	override fun refreshWidgetPositions() {
		super.refreshWidgetPositions()
	//?} else {
	/*override fun initTabNavigation() {
		super.initTabNavigation()*/
	//?}
		layout.refreshPositions()
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)

		val centerX = MCUtils.window.scaledWidth / 2

		RenderUtils.drawCenteredText(context, TITLE_TEXT, centerX, height / 6 - 10, 4.0f, NobaColor.BLUE.toColor().rgb, true)
		RenderUtils.drawCenteredText(context, VERSION_TEXT, centerX, height / 6 + 25, 1.5f, 0xFFFFFF, true)
	}

	override fun close() {
		ElementManager.loadElements()
		super.close()
	}

	private fun openConfig() {
		client?.setScreen(NobaConfigManager.getConfigScreen(this))
	}

	private fun openKeybindsEditor() {
		client!!.setScreen(KeyBindsScreen(this))
	}

	private fun openHudEditor() {
		client?.setScreen(NobaHudScreen(this))
	}
}