package me.nobaboy.nobaaddons.screens

import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.screens.keybinds.KeyBindsScreen
import me.nobaboy.nobaaddons.screens.notifications.ChatNotificationsScreen
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.RenderUtils.drawCenteredText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ConfirmLinkScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget
import net.minecraft.screen.ScreenTexts

class NobaMainScreen(private val parent: Screen? = null) : Screen(CommonText.NOBAADDONS) {
	private val layout: ThreePartsLayoutWidget = ThreePartsLayoutWidget(this, 150, 20)

	companion object {
		private const val GITHUB_ROOT = "https://github.com/nobaboy/NobaAddons"

		private const val SPACING = 8
		private const val BUTTON_WIDTH = 200
		private const val BUTTON_WIDTH_HALF = 96

		private val TITLE_TEXT = CommonText.NOBAADDONS
		private val VERSION_TEXT = "v${NobaAddons.VERSION}"
		private val CONFIGURATION_TEXT = tr("nobaaddons.screen.main.button.config", "Open Config")
		private val EDIT_LOCATIONS_TEXT = tr("nobaaddons.screen.main.button.hud", "Edit HUD")
		private val EDIT_KEYBINDS_TEXT = tr("nobaaddons.screen.main.button.keybinds", "Key Binds")
		private val EDIT_NOTIFICATIONS_TEXT = tr("nobaaddons.screen.main.button.chatNotifications", "Chat Notifications")
		private val SOURCE_TEXT = tr("nobaaddons.screen.main.button.github", "GitHub")
		private val ISSUES_TEXT = tr("nobaaddons.screen.main.button.issues", "Report Issue")
		private val MODRINTH_TEXT = tr("nobaaddons.screen.main.button.modrinth", "Modrinth")
		private val DISCORD_TEXT = tr("nobaaddons.screen.main.button.discord", "Discord")
	}

	override fun init() {
		val gridWidget = layout.addBody(GridWidget()).setSpacing(SPACING)
		gridWidget.mainPositioner.alignHorizontalCenter()
		val adder = gridWidget.createAdder(2)

		adder.add(ButtonWidget.builder(CONFIGURATION_TEXT) { openConfig() }.width(BUTTON_WIDTH).build(), 2)
		if(MCUtils.world != null) adder.add(ButtonWidget.builder(EDIT_LOCATIONS_TEXT) { openHudEditor() }.width(BUTTON_WIDTH).build(), 2)
		adder.add(ButtonWidget.builder(EDIT_NOTIFICATIONS_TEXT) { openNotificationsEditor() }.width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(EDIT_KEYBINDS_TEXT) { openKeybindsEditor() }.width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(SOURCE_TEXT, ConfirmLinkScreen.opening(this, GITHUB_ROOT)).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(ISSUES_TEXT, ConfirmLinkScreen.opening(this, "$GITHUB_ROOT/issues")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(MODRINTH_TEXT, ConfirmLinkScreen.opening(this, "https://modrinth.com/mod/nobaaddons")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(DISCORD_TEXT, ConfirmLinkScreen.opening(this, "https://discord.gg/N9Db3NeWfU")).width(BUTTON_WIDTH_HALF).build())
		adder.add(ButtonWidget.builder(ScreenTexts.DONE) { close() }.width(BUTTON_WIDTH).build(), 2)

		layout.refreshPositions()
		layout.forEachChild { addDrawableChild(it) }
	}

	//? if >=1.21.2 {
	override fun refreshWidgetPositions() {
		super.refreshWidgetPositions()
	//?} else {
	/*override fun initTabNavigation() {
		super.initTabNavigation()
	*///?}
		layout.refreshPositions()
	}

	override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(context, mouseX, mouseY, delta)

		val centerX = MCUtils.window.scaledWidth / 2

		context.drawCenteredText(TITLE_TEXT, centerX, height / 6 - 10, scale = 4f, color = NobaColor.BLUE)
		context.drawCenteredText(VERSION_TEXT, centerX, height / 6 + 25, scale = 1.5f)
	}

	override fun close() {
		client!!.setScreen(parent)
	}

	private fun openConfig() {
		if(parent is YACLScreen) {
			client!!.setScreen(parent)
		} else {
			client!!.setScreen(NobaConfig.getConfigScreen(this))
		}
	}

	private fun openKeybindsEditor() {
		client!!.setScreen(KeyBindsScreen(this))
	}

	private fun openHudEditor() {
		client?.setScreen(NobaHudScreen(this))
	}

	private fun openNotificationsEditor() {
		client?.setScreen(ChatNotificationsScreen(this))
	}
}