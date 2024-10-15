package me.nobaboy.nobaaddons.features.ui.infobox

import me.nobaboy.nobaaddons.config.ui.ElementManager
import me.nobaboy.nobaaddons.config.ui.controllers.InfoBox
import me.nobaboy.nobaaddons.config.ui.elements.Element
import me.nobaboy.nobaaddons.config.ui.elements.TextElement
import me.nobaboy.nobaaddons.utils.RegexUtils.findAllMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import java.util.regex.Pattern

class InfoBoxHud(val infoBox: InfoBox) : TextElement(infoBox.element) {
	val functionPattern = Pattern.compile("(?<function>\\{[A-z0-9]+})")
	val colorCodePattern = Regex("&[0-9a-fklmnor]")

	override fun text(): String = compileString(infoBox.text)
	override fun textMode(): TextMode = infoBox.mode
	override fun outlineColor(): Int = 0x000000

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
		fun newInfoBox(): InfoBox {
			val identifier = ElementManager.newIdentifier("Info Box")
			val infoBox = InfoBox("", TextMode.SHADOW, Element(identifier, 100, 100, 1.0))
			ElementManager.add(InfoBoxHud(infoBox))
			return infoBox
		}
	}
}