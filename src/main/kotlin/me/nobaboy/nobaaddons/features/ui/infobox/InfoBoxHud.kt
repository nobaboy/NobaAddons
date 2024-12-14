package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.screens.hud.elements.TextMode
import me.nobaboy.nobaaddons.screens.hud.elements.data.TextElement
import me.nobaboy.nobaaddons.screens.hud.elements.impl.TextHud
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.findAllMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import java.util.regex.Pattern

class InfoBoxHud(val textElement: TextElement) : TextHud(textElement.element) {
	private val functionPattern = Pattern.compile("(?<function>\\{[A-z0-9]+})")
	private val colorCodePattern = Regex("&&[0-9a-fk-or]")

	override val enabled: Boolean get() = MCUtils.client.currentScreen !is InfoBoxesScreen

	override val text: String get() = compileText(textElement.text)
	override val textMode: TextMode get() = textElement.textMode
	override val outlineColor: Int get() = textElement.outlineColor

	private fun replaceColorCodes(text: String): String {
		return colorCodePattern.replace(text) { matchResult ->
			matchResult.value.replace("&&", "§")
		}
	}

	private fun replaceFunctions(text: String): String {
		var formattedText = text
		functionPattern.findAllMatcher(text) {
			val functionName = group("function") ?: return@findAllMatcher
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