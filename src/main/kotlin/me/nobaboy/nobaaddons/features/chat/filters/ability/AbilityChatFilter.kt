package me.nobaboy.nobaaddons.features.chat.filters.ability

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import java.util.regex.Pattern

object AbilityChatFilter : IChatFilter {
	private val abilityDamagePattern by Regex("Your (?<ability>[A-z' ]+) hit [0-9]+ (enemies|enemy) for [0-9,.]+ damage\\.").fromRepo("filter.abilities.damage")
	private val abilityCooldownPattern by Regex("This ability is on cooldown for [0-9]+s\\.").fromRepo("filter.abilities.cooldown")

	override val enabled: Boolean get() = SkyBlockAPI.inSkyBlock

	override fun shouldFilter(message: String): Boolean {
		abilityDamagePattern.onFullMatch(message) {
			val ability = groups["ability"]?.value ?: return@onFullMatch
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

		abilityCooldownPattern.onFullMatch(message) {
			return config.hideAbilityCooldownMessage
		}

		return false
	}
}