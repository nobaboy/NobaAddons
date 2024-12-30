package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.ui.elements.TextHud
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.ui.elements.data.ElementBounds
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.properties.CacheOf
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class InfoBoxHud(val textElement: InfoBoxElement) : TextHud(textElement) {
	private val functionPattern = Regex("(?<function>\\{[A-z0-9]+})")
	private val colorCodePattern = Regex("&&[0-9a-fk-orz]", RegexOption.IGNORE_CASE)

	override val name: Text = tr("nobaaddons.infoBox", "Info Box")
	override val enabled: Boolean get() = MCUtils.client.currentScreen !is InfoBoxesScreen

	val text: List<Text> by CacheOf(textElement::text) {
		textElement.text.replace("\\n", "\n").split("\n").map { compileText(it).toText() }
	}

	override fun renderText(context: DrawContext) {
		text.forEachIndexed { i, line ->
			renderLine(context, line, y = i * MCUtils.textRenderer.fontHeight)
		}
	}

	override fun getBounds(): ElementBounds = getBoundsFrom(text)

	private fun replaceColorCodes(text: String): String {
		return colorCodePattern.replace(text) { matchResult ->
			matchResult.value.replace("&&", "§")
		}
	}

	private fun replaceFunctions(text: String): String {
		var formattedText = text
		functionPattern.forEachMatch(text) {
			val functionName = groups["function"]?.value ?: return@forEachMatch
			val matchedFunction = InfoBoxFunctions.entries.firstOrNull { it.aliases.any { it.lowercaseEquals(functionName) } }
			val result = matchedFunction?.runnable?.invoke() ?: functionName

			formattedText = formattedText.replace(functionName, result)
		}

		return formattedText
	}

	private fun compileText(text: String): String {
		var formattedText = replaceFunctions(text)
		return replaceColorCodes(formattedText)
	}
}