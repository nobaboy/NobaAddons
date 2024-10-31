package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.config.ui.ElementManager
import me.nobaboy.nobaaddons.config.ui.elements.Element
import me.nobaboy.nobaaddons.config.ui.elements.TextElement
import me.nobaboy.nobaaddons.config.ui.elements.TextMode
import me.nobaboy.nobaaddons.config.ui.elements.impl.TextHud
import me.nobaboy.nobaaddons.utils.RegexUtils.findAllMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import java.util.regex.Pattern

class InfoBoxHud(element: TextElement) : TextHud(element.element) {
	private val functionPattern = Pattern.compile("(?<function>\\{[A-z0-9]+})")
	private val colorCodePattern = Regex("&[0-9a-fk-or]")

	override var text: String = compileText(element.text)
	override var mode: TextMode = element.mode
	override var outlineColor: Int = 0x000000

	private fun replaceColorCodes(text: String): String =
		colorCodePattern.replace(text) { matchResult ->
			matchResult.value.replace("&", "§")
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

	companion object {
		fun createHud(): TextElement {
			val identifier = ElementManager.newIdentifier("Info Box")
			val infoBox = TextElement(identifier, TextMode.SHADOW, Element(identifier, 100, 100))
			ElementManager.add(InfoBoxHud(infoBox))
			return infoBox
		}
	}
}