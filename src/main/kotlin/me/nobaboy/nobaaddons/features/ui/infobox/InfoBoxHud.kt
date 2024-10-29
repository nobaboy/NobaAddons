package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.config.ui.ElementManager
import me.nobaboy.nobaaddons.config.ui.elements.Element
import me.nobaboy.nobaaddons.config.ui.elements.TextHud
import me.nobaboy.nobaaddons.config.ui.elements.TextMode
import me.nobaboy.nobaaddons.config.ui.elements.impl.TextElement
import me.nobaboy.nobaaddons.utils.RegexUtils.findAllMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import java.util.regex.Pattern

class InfoBoxHud(element: TextElement) : TextHud(element.element) {
	val functionPattern = Pattern.compile("(?<function>\\{[A-z0-9]+})")
	val colorCodePattern = Regex("&[0-9a-fk-or]")

	override val text: String = compileString(element.text)
	override val mode: TextMode = element.mode
	override val outlineColor: Int = 0x000000

	fun compileString(string: String): String {
		var formattedString = string
		formattedString = colorCodePattern.replace(formattedString) { matchResult ->
			matchResult.value.replace("&", "§")
		}

		functionPattern.findAllMatcher(formattedString) {
			val functionName = group("function") ?: return@findAllMatcher
			val matchedFunction = InfoBoxFunctions.entries.firstOrNull { it.aliases.any { it.lowercaseEquals(functionName) } }
			val result = matchedFunction?.runnable?.invoke() ?: functionName

			formattedString = formattedString.replace(functionName, result)
		}
		return formattedString
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