package me.nobaboy.nobaaddons.screens.notifications

import me.nobaboy.nobaaddons.features.chat.notifications.ChatNotificationsConfig
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class ChatNotificationsListWidget(
	client: MinecraftClient,
	width: Int,
	height: Int,
	y: Int,
	itemHeight: Int
) : ElementListWidget<ChatNotificationsListWidget.NotificationEntry>(client, width, height, y, itemHeight) {
	private val notifications = mutableListOf<ChatNotificationsConfig.Notification>()
	var hasChanges = false

	init {
		ChatNotificationsConfig.notifications.forEach { notifications.add(it.copy()) }
		refreshEntries()
	}

	fun refreshEntries() {
		clearEntries()
		notifications.forEachIndexed { index, keyBind ->
			addEntry(NotificationEntry(index))
		}

		update()
	}

	fun update() {
		children().forEach(NotificationEntry::update)
	}

	fun create() {
		notifications.add(ChatNotificationsConfig.Notification())
		refreshEntries()
		hasChanges = true
	}

	fun saveChanges() {
		notifications.removeIf { it.message.isBlank() }
		ChatNotificationsConfig.notifications.clear()
		ChatNotificationsConfig.notifications.addAll(notifications)
		ChatNotificationsConfig.save()
		hasChanges = false
	}

	override fun removeEntry(entry: NotificationEntry): Boolean {
		return super.removeEntry(entry)
	}

	override fun getRowWidth(): Int = super.rowWidth + 80
	override fun getScrollbarX(): Int = super.scrollbarX + 20

	inner class NotificationEntry(private val index: Int) : Entry<NotificationEntry>() {
		private val notification = notifications[index]
		private var oldScrollAmount = 0.0

		private val toggleText: Text
			get() = if(notification.enabled) "Enabled".toText().green() else "Disabled".toText().red()

		private val messageField = TextFieldWidget(client.textRenderer, 158, 20, Text.empty()).apply {
			text = notification.message
			setMaxLength(256)
			setPlaceholder(tr("nobaaddons.screen.chatNotifications.chatMessage", "Chat Message"))
			tooltip = Tooltip.of(tr("nobaaddons.screen.chatNotifications.chatMessage.tooltip", "The chat message to display a notification for upon receiving"))
			setChangedListener { newText ->
				notification.message = newText
				hasChanges = true
			}
		}

		private val displayField = TextFieldWidget(client.textRenderer, 158, 20, Text.empty()).apply {
			text = notification.display
			setMaxLength(256)
			setPlaceholder(tr("nobaaddons.screen.chatNotifications.displayMessage", "Notification"))
			tooltip = Tooltip.of(tr("nobaaddons.screen.chatNotifications.displayMessage.tooltip", "The notification to display on screen; supports color codes (and regex groups when using Regex)"))
			setChangedListener { newText ->
				notification.display = newText
				hasChanges = true
			}
		}

		private val toggleButton = ButtonWidget.builder(toggleText) {
			changeToggle()
		}.size(104, 20).build()

		private val modeButton = ButtonWidget.builder(notification.mode.toString().toText()) {
			changeMode()
		}.size(104, 20).build()

		private val deleteButton = ButtonWidget.builder(CommonText.SCREEN_DELETE) {
			oldScrollAmount = /*? if >=1.21.4 {*/scrollY/*?} else {*//*scrollAmount*//*?}*/
			deleteEntry()
		}.size(104, 20).build()

		init {
			update()
		}

		private fun changeToggle() {
			notification.enabled = !notification.enabled
			toggleButton.message = toggleText

			refreshEntries()
			hasChanges = true
		}

		private fun changeMode() {
			val newMode = notification.mode.next
			notification.mode = newMode
			modeButton.message = newMode.toString().toText()

			refreshEntries()
			hasChanges = true
		}


		private fun deleteEntry() {
			notifications.removeAt(index)
			removeEntry(this)

			/*? if >=1.21.4 {*/scrollY/*?} else {*//*scrollAmount*//*?}*/ = oldScrollAmount

			refreshEntries()
			hasChanges = true
		}

		override fun children(): List<Element> = listOf(messageField, displayField, toggleButton, modeButton, deleteButton)
		override fun selectableChildren(): List<Selectable> = listOf(messageField, displayField, toggleButton, modeButton, deleteButton)

		override fun render(
			context: DrawContext,
			index: Int,
			y: Int,
			x: Int,
			entryWidth: Int,
			entryHeight: Int,
			mouseX: Int,
			mouseY: Int,
			hovered: Boolean,
			tickDelta: Float
		) {
			messageField.y = y
			messageField.render(context, mouseX, mouseY, tickDelta)

			displayField.y = y
			displayField.render(context, mouseX, mouseY, tickDelta)

			toggleButton.y = y + 24
			toggleButton.render(context, mouseX, mouseY, tickDelta)

			modeButton.y = y + 24
			modeButton.render(context, mouseX, mouseY, tickDelta)

			deleteButton.y = y + 24
			deleteButton.render(context, mouseX, mouseY, tickDelta)
		}

		fun update() {
			messageField.x = width / 2 - 160
			displayField.x = width / 2 + 2
			toggleButton.x = width / 2 - 160
			modeButton.x = width / 2 - 52
			deleteButton.x = width / 2 + 56
		}
	}
}