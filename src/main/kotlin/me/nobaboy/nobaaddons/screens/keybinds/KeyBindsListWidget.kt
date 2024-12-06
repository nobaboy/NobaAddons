package me.nobaboy.nobaaddons.screens.keybinds

import me.nobaboy.nobaaddons.screens.keybinds.impl.KeyBind
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
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
) : ElementListWidget<KeyBindsListWidget.AbstractKeyBindEntry>(client, width, height, y, itemHeight) {
	private val keyBinds = mutableListOf<KeyBind>()
	val size: Int get() = keyBinds.size

	var hasChanges = false

	init {
		KeyBindsManager.commandKeyBinds.forEach {
			keyBinds.add(it.copy())
		}

		refreshEntries()
	}

	fun refreshEntries() {
		clearEntries()
		keyBinds.forEachIndexed { index, keyBind ->
			addEntry(KeyBindEntry(index))
		}

		update()
	}

	fun update() {
		children().forEach(AbstractKeyBindEntry::update)
	}

	fun addKeyBind() {
		keyBinds.add(KeyBind())
		screen.addButton.active = keyBinds.size < 20

		refreshEntries()
		hasChanges = true
	}

	fun saveChanges() {
		keyBinds.removeIf { it.command.isBlank() }
		KeyBindsManager.commandKeyBinds.clear()
		KeyBindsManager.commandKeyBinds.addAll(keyBinds)
		KeyBindsManager.saveKeyBinds()

		hasChanges = false
	}

	override fun removeEntry(entry: AbstractKeyBindEntry): Boolean {
		return super.removeEntry(entry)
	}

	override fun getRowWidth(): Int = super.rowWidth + 140
	override fun getScrollbarX(): Int = super.scrollbarX + 20

	inner class KeyBindEntry(private val keyBindIndex: Int) : AbstractKeyBindEntry() {
		private val keyBind = keyBinds[keyBindIndex]
		private var duplicate = false

		private val textField = TextFieldWidget(client.textRenderer, 210, 20, Text.empty()).apply {
			setMaxLength(128)
			text = keyBind.command
			tooltip = Tooltip.of(Text.translatable("nobaaddons.screen.keyBinds.command.tooltip"))
			setChangedListener { newText ->
				keyBind.command = newText
				hasChanges = true
			}
		}

		private val editButton = ButtonWidget.builder(Text.empty()) {
			screen.selectedKeyBind = keyBind
			hasChanges = true
			update()
		}.size(75, 20).build()

		private val deleteButton = ButtonWidget.builder(Text.translatable("nobaaddons.screen.button.delete")) {
			deleteEntry()
		}.size(50, 20).build()

		init {
			update()
		}

		private fun deleteEntry() {
			keyBinds.removeAt(keyBindIndex)
			removeEntry(this)

			screen.addButton.active = size < 20

			refreshEntries()
			hasChanges = true
		}

		private fun getKeyText(keyCode: Int): Text {
			return when(keyCode) {
				in GLFW.GLFW_MOUSE_BUTTON_1..GLFW.GLFW_MOUSE_BUTTON_8 -> InputUtil.Type.MOUSE.createFromCode(keyCode).localizedText
				else -> InputUtil.Type.KEYSYM.createFromCode(keyCode).localizedText
			}
		}

		override fun children(): List<Element> = listOf(textField, editButton, deleteButton)
		override fun selectableChildren(): List<Selectable> = listOf(textField, editButton, deleteButton)

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

			editButton.y = y
			editButton.render(context, mouseX, mouseY, tickDelta)

			deleteButton.y = y
			deleteButton.render(context, mouseX, mouseY, tickDelta)

			if(duplicate) context.fill(editButton.x - 6, y, editButton.x - 3, y + 20, 0xFFFF0000.toInt())
		}

		override fun update() {
			textField.x = width / 2 - 180
			editButton.x = width / 2 + 50
			deleteButton.x = width / 2 + 130

			duplicate = false
			editButton.message = getKeyText(keyBind.keyCode)

			if(keyBind.keyCode != GLFW.GLFW_KEY_UNKNOWN) {
				val filteredKeyBinds = keyBinds.filterIndexed { index, _ -> index != keyBindIndex }
				duplicate = filteredKeyBinds.any { it.keyCode == keyBind.keyCode }
			}

			if(duplicate) editButton.apply {
				message = buildText {
					formatted(Formatting.RED)

					append("[ ")
					append(message.copy().formatted(Formatting.WHITE))
					append(" ]")
				}
			}

			if(screen.selectedKeyBind == keyBind) editButton.apply {
				message = buildText {
					formatted(Formatting.YELLOW)

					append("> ")
					append(message.copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
					append(" <")
				}
			}
		}
	}

	abstract class AbstractKeyBindEntry : Entry<AbstractKeyBindEntry>() {
		abstract fun update()
	}
}