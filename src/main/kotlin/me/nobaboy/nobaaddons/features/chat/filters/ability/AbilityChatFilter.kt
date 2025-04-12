package me.nobaboy.nobaaddons.features.chat.filters.ability

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch

object AbilityChatFilter : IChatFilter {
	private val ABILITY_DAMAGE_REGEX by Repo.regex(
		"filter.abilities.damage",
		"Your (?<ability>[A-z' ]+) hit [0-9]+ (enemies|enemy) for [0-9,.]+ damage\\."
	)
	private val ABILITY_COOLDOWN_REGEX by Repo.regex(
		"filter.abilities.cooldown",
		"This ability is on cooldown for [0-9]+s\\."
	)

	override val enabled: Boolean get() = SkyBlockAPI.inSkyBlock

	override fun shouldFilter(message: String): Boolean {
		ABILITY_DAMAGE_REGEX.onFullMatch(message) {
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

		ABILITY_COOLDOWN_REGEX.onFullMatch(message) {
			return config.hideAbilityCooldownMessage
		}

		return false
	}
}