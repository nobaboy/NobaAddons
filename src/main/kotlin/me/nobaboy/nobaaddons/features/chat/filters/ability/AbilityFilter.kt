package me.nobaboy.nobaaddons.features.chat.filters.ability

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import net.minecraft.text.Text
import java.util.regex.Pattern

object AbilityFilter : IFilter {
	private val abilityDamagePattern = Pattern.compile("Your (?<ability>[A-z' ]+) hit [0-9]+ (enemies|enemy) for [0-9,.]+ damage\\.")
	private val abilityCooldownPattern = Pattern.compile("This ability is on cooldown for [0-9]+s\\.")

	override fun shouldFilter(message: Text, text: String): Boolean {
		abilityDamagePattern.matchMatcher(text) {
			val ability = group("ability")
			return when (ability) {
				"Implosion" -> config.hideImplosionDamageMessage
				"Molten Wave" -> config.hideMoltenWaveDamageMessage
				"Spirit Sceptre" -> config.hideSpiritSceptreDamageMessage
				"Giant's Sword" -> config.hideGiantSwordDamageMessage
				"Livid Dagger" -> config.hideLividDaggerDamageMessage
				"Ray of Hope" -> config.hideRayOfHopeDamageMessage
				else -> false
			}
		}

		abilityCooldownPattern.matchMatcher(text) {
			return config.hideAbilityCooldownMessage
		}

		return false
	}

	override fun isEnabled(): Boolean = SkyBlockAPI.inSkyblock
}