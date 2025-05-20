package me.nobaboy.nobaaddons.screens.notifications

import me.nobaboy.nobaaddons.utils.ScreenUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.drawCenteredText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.client.gui.widget.SimplePositioningWidget
import net.minecraft.screen.ScreenTexts

// TODO abstract this
class ChatNotificationsScreen(private val parent: Screen?) : Screen(tr("nobaaddons.screen.chatNotifications", "Chat Notifications")) {
	private lateinit var notificationsList: ChatNotificationsListWidget
	private var initialized = false

	private val cancelButton = ButtonWidget.builder(ScreenTexts.CANCEL) { close() }.build()
	private val addButton = ButtonWidget.builder(tr("nobaaddons.screen.button.newNotification", "New Notification")) { notificationsList.create() }.build()
	private val doneButton = ButtonWidget.builder(ScreenTexts.DONE) {
		notificationsList.saveChanges()
		close()
	}.build()

	override fun init() {
		super.init()

		if(!initialized) {
			notificationsList = ChatNotificationsListWidget(client!!, width, height - 96, 33, 56)
			initialized = true
		}

		notificationsList.setDimensions(width, height - 96)
		notificationsList.update()

		addDrawableChild(notificationsList)

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
		context.drawCenteredText(title, width / 2, 12)
	}

	override fun close() {
		if(!notificationsList.hasChanges) {
			client!!.setScreen(parent)
			return
		}

		ScreenUtils.confirmClose(this) {
			client!!.setScreen(parent)
			initialized = false
		}
	}
}