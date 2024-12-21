package me.nobaboy.nobaaddons.features.chat.filters.ability

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import java.util.regex.Pattern

object AbilityChatFilter : IChatFilter {
	private val abilityDamagePattern = Pattern.compile("Your (?<ability>[A-z' ]+) hit [0-9]+ (enemies|enemy) for [0-9,.]+ damage\\.")
	private val abilityCooldownPattern = Pattern.compile("This ability is on cooldown for [0-9]+s\\.")

	override val enabled: Boolean get() = SkyBlockAPI.inSkyBlock

	override fun shouldFilter(message: String): Boolean {
		abilityDamagePattern.matchMatcher(message) {
			val ability = group("ability")
			return when(ability) {
				"Implosion" -> config.hideImplosionDamageMessage
				"Molten Wave" -> config.hideMoltenWaveDamageMessage
				"Spirit Sceptre" -> config.hideGuidedBatDamageMessage
				"Giant's Sword" -> config.hideGiantsSlamDamageMessage
				"Livid Dagger" -> config.hideThrowDamageMessage
				"Ray of Hope" -> config.hideRayOfHopeDamageMessage
				else -> false
			}
		}

		abilityCooldownPattern.matchMatcher(message) {
			return config.hideAbilityCooldownMessage
		}

		return false
	}
}