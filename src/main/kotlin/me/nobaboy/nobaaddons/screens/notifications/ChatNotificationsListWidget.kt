package me.nobaboy.nobaaddons.screens.notifications

import me.nobaboy.nobaaddons.features.chat.notifications.ChatNotificationsConfig
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
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
	private val notifs = mutableListOf<ChatNotificationsConfig.Notification>()
	var hasChanges = false

	init {
		ChatNotificationsConfig.notifications.forEach { notifs.add(it.copy()) }
		refreshEntries()
	}

	fun refreshEntries() {
		clearEntries()
		notifs.forEachIndexed { index, keyBind ->
			addEntry(NotificationEntry(index))
		}

		update()
	}

	fun update() {
		children().forEach(NotificationEntry::update)
	}

	fun create() {
		notifs.add(ChatNotificationsConfig.Notification())
		refreshEntries()
		hasChanges = true
	}

	fun saveChanges() {
		notifs.removeIf { it.message.isBlank() }
		ChatNotificationsConfig.notifications.clear()
		ChatNotificationsConfig.notifications.addAll(notifs)
		ChatNotificationsConfig.save()
		hasChanges = false
	}

	override fun removeEntry(entry: NotificationEntry): Boolean {
		return super.removeEntry(entry)
	}

	override fun getRowWidth(): Int = super.rowWidth + 140
	override fun getScrollbarX(): Int = super.scrollbarX + 20

	inner class NotificationEntry(private val index: Int) : Entry<NotificationEntry>() {
		private var oldScrollAmount = 0.0
		private val notif = notifs[index]

		private val messageField = TextFieldWidget(client.textRenderer, 200, 20, Text.empty()).apply {
			setMaxLength(256)
			text = notif.message
			setPlaceholder(tr("nobaaddons.screen.chatNotifications.chatMessage", "Chat message (regex)"))
			setChangedListener { newText ->
				notif.message = newText
				hasChanges = true
			}
		}

		private val displayMessageField = TextFieldWidget(client.textRenderer, 200, 20, Text.empty()).apply {
			setMaxLength(256)
			text = notif.displayMessage
			setPlaceholder(tr("nobaaddons.screen.chatNotifications.displayMessage", "Notification"))
			setChangedListener { newText ->
				notif.displayMessage = newText
				hasChanges = true
			}
		}

		private val deleteButton = ButtonWidget.builder(CommonText.SCREEN_DELETE) {
			// FIXME this isn't clickable and I don't know why someone please fix this for me
			oldScrollAmount = /*? if >=1.21.4 {*/scrollY/*?} else {*//*scrollAmount*//*?}*/
			deleteEntry()
		}.size(50, 20).build()

		init {
			update()
		}

		private fun deleteEntry() {
			notifs.removeAt(index)
			removeEntry(this)

			/*? if >=1.21.4 {*/scrollY/*?} else {*//*scrollAmount*//*?}*/ = oldScrollAmount

			refreshEntries()
			hasChanges = true
		}

		override fun children(): List<Element> = listOf(messageField, displayMessageField, deleteButton)
		override fun selectableChildren(): List<Selectable> = listOf(messageField, displayMessageField, deleteButton)

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

			displayMessageField.y = y
			displayMessageField.render(context, mouseX, mouseY, tickDelta)

			deleteButton.y = y
			deleteButton.render(context, mouseX, mouseY, tickDelta)
		}

		fun update() {
			messageField.x = width / 2 - 180 - 50
			displayMessageField.x = width / 2 - 25
			deleteButton.x = width / 2 + 130 + 60
		}
	}
}