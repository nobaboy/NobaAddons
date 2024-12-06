package me.nobaboy.nobaaddons.screens.infoboxes

import me.nobaboy.nobaaddons.screens.hud.elements.data.TextElement
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class InfoBoxesListWidget(
	client: MinecraftClient,
	private val screen: InfoBoxesScreen,
	width: Int,
	height: Int,
	y: Int,
	itemHeight: Int
) : ElementListWidget<InfoBoxesListWidget.AbstractInfoBoxEntry>(client, width, height, y, itemHeight) {
	private val infoBoxes = mutableListOf<TextElement>()
	val size: Int get() = infoBoxes.size

	var hasChanges = false

	init {
		InfoBoxesManager.infoBoxes.forEach {
			infoBoxes.add(it.copy())
		}

		refreshEntries()
	}

	fun refreshEntries() {
		clearEntries()
		infoBoxes.forEachIndexed { index, _ ->
			addEntry(InfoBoxConfigEntry(index))
		}

		update()
	}

	fun update() {
		children().forEach(AbstractInfoBoxEntry::update)
	}

	fun addInfoBox() {
		val newInfoBox = InfoBoxesManager.getNewInfoBox(infoBoxes.size + 1)
		infoBoxes.add(newInfoBox)

		screen.addButton.active = infoBoxes.size < 20

		refreshEntries()
		hasChanges = true
	}

	private fun regenerateIdentifiers() {
		val updatedInfoBoxes = infoBoxes.mapIndexed { index, infoBox ->
			val updatedElement = infoBox.element.copy(identifier = "Info Box ${index + 1}")
			infoBox.copy(element = updatedElement)
		}

		infoBoxes.clear()
		infoBoxes.addAll(updatedInfoBoxes)
	}

	fun saveChanges() {
		infoBoxes.removeIf { it.text.isBlank() }
		regenerateIdentifiers()

		InfoBoxesManager.infoBoxes.clear()
		InfoBoxesManager.infoBoxes.addAll(infoBoxes)
		InfoBoxesManager.saveInfoBoxes()

		hasChanges = false
	}

	override fun removeEntry(entry: AbstractInfoBoxEntry): Boolean {
		return super.removeEntry(entry)
	}

	override fun getRowWidth(): Int = super.rowWidth + 140
	override fun getScrollbarX(): Int = super.scrollbarX + 20

	inner class InfoBoxConfigEntry(private val infoBoxIndex: Int) : AbstractInfoBoxEntry() {
		val infoBox = infoBoxes[infoBoxIndex]

		private val textField = TextFieldWidget(client.textRenderer, 250, 20, Text.empty()).apply {
			setMaxLength(256)
			text = infoBox.text
			setChangedListener { newText ->
				infoBox.text = newText
				hasChanges = true
			}
		}

		private val textModeButton = ButtonWidget.builder(Text.literal(infoBox.textMode.toString())) {
			changeTextMode()
		}.size(50, 20).build()

		private val deleteButton = ButtonWidget.builder(Text.translatable("nobaaddons.screen.button.delete")) {
			deleteEntry()
		}.size(50, 20).build()

		init {
			update()
		}

		private fun changeTextMode() {
			val newTextMode = infoBox.textMode.next
			infoBox.textMode = newTextMode
			textModeButton.message = Text.literal(newTextMode.toString())

			refreshEntries()
			hasChanges = true
		}

		fun deleteEntry() {
			infoBoxes.removeAt(infoBoxIndex)
			removeEntry(this)

			screen.addButton.active = size < 20

			regenerateIdentifiers()
			refreshEntries()

			hasChanges = true
		}

		override fun children(): List<Element> = listOf(textField, textModeButton, deleteButton)
		override fun selectableChildren(): List<Selectable> = listOf(textField, textModeButton, deleteButton)

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

			textModeButton.y = y
			textModeButton.render(context, mouseX, mouseY, tickDelta)

			deleteButton.y = y
			deleteButton.render(context, mouseX, mouseY, tickDelta)

			RenderUtils.drawCenteredText(context, infoBox.element.identifier, width / 2 - 180 - 35, y + 6)
		}

		override fun update() {
			textField.x = width / 2 - 180
			textModeButton.x = width / 2 + 75
			deleteButton.x = width / 2 + 130
		}
	}

	abstract class AbstractInfoBoxEntry : Entry<AbstractInfoBoxEntry>() {
		abstract fun update()
	}
}