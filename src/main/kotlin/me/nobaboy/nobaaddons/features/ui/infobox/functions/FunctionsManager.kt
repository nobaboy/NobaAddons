package me.nobaboy.nobaaddons.features.ui.infobox.functions

import me.nobaboy.nobaaddons.features.ui.infobox.functions.impl.MinecraftFunctions
import me.nobaboy.nobaaddons.features.ui.infobox.functions.impl.PlayerFunctions
import me.nobaboy.nobaaddons.features.ui.infobox.functions.impl.SkyBlockFunctions
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch

// TODO: could do with logical functions? well formatting is priority right now
object FunctionsManager {
	private val functionPattern = Regex("\\{(?<function>[A-z]+)}")
	private val functions = mutableMapOf<String, InfoBoxFunction<*>>()

	init {
		listOf(
			MinecraftFunctions.PingFunction,
			MinecraftFunctions.FpsFunction,
			MinecraftFunctions.DayFunction,

			PlayerFunctions.XFunction,
			PlayerFunctions.YFunction,
			PlayerFunctions.ZFunction,
			PlayerFunctions.PitchFunction,
			PlayerFunctions.YawFunction,
			PlayerFunctions.BpsFunction,

			SkyBlockFunctions.LevelFunction,
			SkyBlockFunctions.LevelColorFunction,
			SkyBlockFunctions.XPFunction,
			SkyBlockFunctions.CoinsFunction,
			SkyBlockFunctions.BitsFunction,
			SkyBlockFunctions.ZoneFunction
		).forEach { function ->
			functions[function.name] = function
			function.aliases.forEach { alias ->
				functions[alias] = function
			}
		}
	}

	fun processText(text: String): String {
		var result = text

		functionPattern.forEachMatch(result) {
			val functionName = groups["function"]?.value?.lowercase() ?: return@forEachMatch
			val function = functions[functionName] ?: return@forEachMatch

			var replacement = function.execute() ?: return@forEachMatch
			if(replacement is Number) replacement = replacement.addSeparators() // Extract into number formatting function

			result = result.replace("{$functionName}", replacement.toString())
		}

		return result
	}
}