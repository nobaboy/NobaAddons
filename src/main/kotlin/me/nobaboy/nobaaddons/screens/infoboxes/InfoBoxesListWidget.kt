package me.nobaboy.nobaaddons.screens.infoboxes

import me.nobaboy.nobaaddons.screens.hud.elements.data.TextElement
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.Selectable.SelectionType
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class InfoBoxesListWidget(
	client: MinecraftClient,
	width: Int,
	height: Int,
	y: Int,
	itemHeight: Int
) : ElementListWidget<InfoBoxesListWidget.AbstractInfoBoxEntry>(client, width, height, y, itemHeight) {
	private val infoBoxes = mutableListOf<TextElement>()
	var hasChanges: Boolean = false
		private set

	init {
		infoBoxes.clear()
		infoBoxes.addAll(InfoBoxesManager.infoBoxes)

		refreshEntries()
	}

	private fun refreshEntries() {
		clearEntries()
		infoBoxes.forEachIndexed { index, _ ->
			addEntry(InfoBoxConfigEntry(index))
		}
	}

	fun addInfoBox() {
		val infoBox = InfoBoxesManager.getNewInfoBox(infoBoxes)
		infoBoxes.add(infoBox)

		refreshEntries()

		hasChanges = true
	}

	fun saveChanges() {
		infoBoxes.removeIf { it.text.isEmpty() }
		InfoBoxesManager.infoBoxes.clear()
		InfoBoxesManager.infoBoxes.addAll(infoBoxes)

		regenerateIdentifiers()

		InfoBoxesManager.saveInfoBoxes()
		hasChanges = false
	}

	private fun regenerateIdentifiers() {
		val updatedInfoBoxes = infoBoxes.mapIndexed { index, infoBox ->
			val updatedElement = infoBox.element.copy(identifier = "Info Box ${index + 1}")
			infoBox.copy(element = updatedElement)
		}

		infoBoxes.clear()
		infoBoxes.addAll(updatedInfoBoxes)
	}

	override fun getRowWidth(): Int = super.rowWidth + 180
	override fun getScrollbarX(): Int = super.scrollbarX + 100

	override fun removeEntry(entry: AbstractInfoBoxEntry): Boolean {
		hasChanges = true
		return super.removeEntry(entry)
	}

	abstract class AbstractInfoBoxEntry : Entry<AbstractInfoBoxEntry>()

	private inner class InfoBoxConfigEntry(private val infoBoxIndex: Int) : AbstractInfoBoxEntry() {
		val infoBox = infoBoxes[infoBoxIndex]

		private val textField = TextFieldWidget(client.textRenderer, width / 2 - 200, 70, 200, 20, Text.empty()).apply {
			setMaxLength(512)
			text = infoBox.text
			setChangedListener { newText ->
				val updatedElement = infoBox.element.copy()
				val updatedInfoBox = infoBox.copy(text = newText, element = updatedElement)

				infoBoxes[infoBoxIndex] = updatedInfoBox
				hasChanges = true
			}
		}

//		private val textModeButton = ButtonWidget.builder()

		private val deleteButton = ButtonWidget.builder(Text.translatable("nobaaddons.screen.button.delete")) {
			deleteEntry()
		}.dimensions(width / 2 + 180, 70, 50, 20).build()

		private fun deleteEntry() {
			infoBoxes.removeAt(infoBoxIndex)
			removeEntry(this)

			regenerateIdentifiers()
			refreshEntries()

			hasChanges = true
		}

		override fun children(): List<Element> = listOf(textField, deleteButton)
		override fun selectableChildren(): List<Selectable> {
			return listOf(object : Selectable {
				override fun getType() = SelectionType.HOVERED
				override fun appendNarrations(builder: NarrationMessageBuilder) {
					builder.put(NarrationPart.TITLE, infoBox.element.identifier)
				}
			})
		}

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
			deleteButton.y = y
			deleteButton.render(context, mouseX, mouseY, tickDelta)

			textField.y = y
			textField.render(context, mouseX, mouseY, tickDelta)

			RenderUtils.drawText(context, infoBox.element.identifier, width / 2 - 265, y + 6)
		}
	}
}