package me.nobaboy.nobaaddons.screens.keybinds

import me.nobaboy.nobaaddons.features.keybinds.KeyBindsManager
import me.nobaboy.nobaaddons.features.keybinds.impl.KeyBind
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.lwjgl.glfw.GLFW

class KeyBindsListWidget(
	client: MinecraftClient,
	private val screen: KeyBindsScreen,
	width: Int,
	height: Int,
	y: Int,
	itemHeight: Int
) : ElementListWidget<KeyBindsListWidget.KeyBindEntry>(client, width, height, y, itemHeight) {
	private val keyBinds = KeyBindsManager.commandKeyBinds.map { it.copy() }.toMutableList()
	var hasChanges = false

	init {
		refreshEntries()
	}

	fun refreshEntries() {
		clearEntries()
		keyBinds.forEachIndexed { index, _ -> addEntry(KeyBindEntry(index)) }
		update()
	}

	fun update() {
		children().forEach(KeyBindEntry::update)
	}

	fun create() {
		keyBinds.add(KeyBind())
		refreshEntries()
		hasChanges = true
	}

	fun saveChanges() {
		keyBinds.removeIf { it.command.isBlank() }

		KeyBindsManager.commandKeyBinds.clear()
		KeyBindsManager.commandKeyBinds.addAll(keyBinds)
		KeyBindsManager.save()

		hasChanges = false
	}

	override fun removeEntry(entry: KeyBindEntry): Boolean {
		return super.removeEntry(entry)
	}

	override fun getRowWidth(): Int = super.rowWidth + 140 // 360
	override fun getScrollbarX(): Int = super.scrollbarX + 20

	inner class KeyBindEntry(private val keyBindIndex: Int) : Entry<KeyBindEntry>() {
		private val keyBind = keyBinds[keyBindIndex]
		private var oldScrollAmount = 0.0
		private var duplicate = false

		// using property access syntax for setMaxLength() is impossible, as kotlin tries to use the
		// private underlying field instead of the correct setter method
		@Suppress("UsePropertyAccessSyntax")
		private val textField = TextFieldWidget(client.textRenderer, 210, 20, Text.empty()).apply {
			text = keyBind.command
			tooltip = Tooltip.of(tr("nobaaddons.screen.keyBinds.command.tooltip", "The command to send when the key bind is pressed"))
			setMaxLength(128)
			setChangedListener { newText ->
				keyBind.command = newText
				hasChanges = true
			}
		}

		private val keyButton = ButtonWidget.builder(Text.empty()) {
			screen.selectedKeyBind = keyBind
			update()
		}.size(75, 20).build()

		private val deleteButton = ButtonWidget.builder(CommonText.SCREEN_DELETE) {
			oldScrollAmount = /*? if >=1.21.4 {*/scrollY/*?} else {*//*scrollAmount*//*?}*/
			deleteEntry()
		}.size(50, 20).build()

		private fun deleteEntry() {
			keyBinds.removeAt(keyBindIndex)
			removeEntry(this)

			/*? if >=1.21.4 {*/scrollY/*?} else {*//*scrollAmount*//*?}*/ = oldScrollAmount

			refreshEntries()
			hasChanges = true
		}

		fun update() {
			textField.x = width / 2 - 180
			keyButton.x = width / 2 + 50
			deleteButton.x = width / 2 + 130

			duplicate = false
			keyButton.message = getKeyText(keyBind.key)

			if(keyBind.key != GLFW.GLFW_KEY_UNKNOWN) {
				val filteredKeyBinds = keyBinds.filterIndexed { index, _ -> index != keyBindIndex }
				duplicate = filteredKeyBinds.any { it.key == keyBind.key }
			}

			if(duplicate) keyButton.apply {
				message = buildText {
					formatted(Formatting.RED)

					append("[ ")
					append(message.copy().formatted(Formatting.WHITE))
					append(" ]")
				}
			}

			if(screen.selectedKeyBind == keyBind) keyButton.apply {
				message = buildText {
					formatted(Formatting.YELLOW)

					append("> ")
					append(message.copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
					append(" <")
				}
			}
		}

		private fun getKeyText(keyCode: Int): Text {
			return when(keyCode) {
				in GLFW.GLFW_MOUSE_BUTTON_1..GLFW.GLFW_MOUSE_BUTTON_8 -> InputUtil.Type.MOUSE.createFromCode(keyCode).localizedText
				else -> InputUtil.Type.KEYSYM.createFromCode(keyCode).localizedText
			}
		}

		override fun children(): List<Element> = listOf(textField, keyButton, deleteButton)
		override fun selectableChildren(): List<Selectable> = listOf(textField, keyButton, deleteButton)

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
			textField.y = y
			textField.render(context, mouseX, mouseY, tickDelta)

			keyButton.y = y
			keyButton.render(context, mouseX, mouseY, tickDelta)

			deleteButton.y = y
			deleteButton.render(context, mouseX, mouseY, tickDelta)

			if(duplicate) context.fill(keyButton.x - 6, y, keyButton.x - 3, y + 20, 0xFFFF0000.toInt())
		}
	}
}