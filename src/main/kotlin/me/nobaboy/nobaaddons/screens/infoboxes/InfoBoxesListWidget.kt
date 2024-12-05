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
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text

class InfoBoxesListWidget(
	client: MinecraftClient,
	width: Int,
	height: Int,
	y: Int,
	itemHeight: Int
) : ElementListWidget<InfoBoxesListWidget.AbstractInfoBoxEntry>(client, width, height, y, itemHeight) {
	private val infoBoxes = mutableListOf<TextElement>()
	private val maxLimitToast = SystemToast(
		SystemToast.Type.PERIODIC_NOTIFICATION,
		Text.literal("Max Limit Reached"),
		Text.literal("You can have up to 20 Info Boxes at a time")
	)

	init {
		infoBoxes.addAll(InfoBoxesManager.infoBoxes)
		infoBoxes.forEachIndexed { index, _ ->
			addEntry(InfoBoxConfigEntry(index))
		}
	}

	fun refreshEntries() {
		clearEntries()
		infoBoxes.forEachIndexed { index, _ ->
			addEntry(InfoBoxConfigEntry(index))
		}
	}

	fun addInfoBox() {
		if(infoBoxes.size >= 20) {
			client.toastManager.clear()
			client.toastManager.add(maxLimitToast)
			return
		}

		val newInfoBox = InfoBoxesManager.getNewInfoBox(infoBoxes)
		infoBoxes.add(newInfoBox)

		refreshEntries()
	}

	fun saveChanges() {
		infoBoxes.removeIf { it.text.isEmpty() }
		regenerateIdentifiers()

		InfoBoxesManager.infoBoxes.clear()
		InfoBoxesManager.infoBoxes.addAll(infoBoxes)
		InfoBoxesManager.saveInfoBoxes()
	}

	private fun regenerateIdentifiers() {
		val updatedInfoBoxes = infoBoxes.mapIndexed { index, infoBox ->
			val updatedElement = infoBox.element.copy(identifier = "Info Box ${index + 1}")
			infoBox.copy(element = updatedElement)
		}

		infoBoxes.clear()
		infoBoxes.addAll(updatedInfoBoxes)
	}

	override fun getRowWidth(): Int = super.rowWidth + 140 // 360
	override fun getScrollbarX(): Int = super.scrollbarX + 100

	abstract class AbstractInfoBoxEntry : Entry<AbstractInfoBoxEntry>()

	private inner class InfoBoxConfigEntry(private val infoBoxIndex: Int) : AbstractInfoBoxEntry() {
		val infoBox = infoBoxes[infoBoxIndex]

		private val textField = TextFieldWidget(client.textRenderer, width / 2 - 180, 70, 280, 20, Text.empty())

		private val textModeButton = ButtonWidget.builder(infoBox.textMode.displayName) {
			changeTextMod()
		}.dimensions(width / 2 + 105, 70, 50, 20).build()

		private val deleteButton = ButtonWidget.builder(Text.literal("✖")) {
			deleteEntry()
		}.dimensions(width / 2 + 160, 70, 20, 20).build()

		init {
			textField.setMaxLength(256)
			textField.text = infoBox.text
			textField.setChangedListener { newText ->
				val updatedInfoBox = infoBox.copy(text = newText)
				infoBoxes[infoBoxIndex] = updatedInfoBox
			}
		}

		private fun changeTextMod() {
			val newTextMode = infoBox.textMode.next
			val updatedInfoBox = infoBox.copy(textMode = newTextMode)

			infoBoxes[infoBoxIndex] = updatedInfoBox
			textModeButton.message = newTextMode.displayName

			refreshEntries()
		}

		private fun deleteEntry() {
			infoBoxes.removeAt(infoBoxIndex)
			removeEntry(this)

			regenerateIdentifiers()
			refreshEntries()
		}

		override fun children(): List<Element> = listOf(textField, textModeButton, deleteButton)
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
			textField.y = y
			textField.render(context, mouseX, mouseY, tickDelta)

			textModeButton.y = y
			textModeButton.render(context, mouseX, mouseY, tickDelta)

			deleteButton.y = y
			deleteButton.render(context, mouseX, mouseY, tickDelta)

			RenderUtils.drawCenteredText(context, infoBox.element.identifier, width / 2 - 180 - 35, y + 6)
		}
	}
}