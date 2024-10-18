package me.nobaboy.nobaaddons.features.chat.filters.general

import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.features.chat.filters.IFilter
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import net.minecraft.text.Text
import java.util.regex.Pattern

object AbilityDamageFilter : IFilter {
	private val abilityDamagePattern = Pattern.compile("Your [A-z ]+ hit [0-9]+ (enemies|enemy) for [0-9,.]+ damage.")

	override fun shouldFilter(message: Text, text: String): Boolean = abilityDamagePattern.matches(text)
	override fun isEnabled(): Boolean = SkyblockAPI.inSkyblock && config.hideAbilityDamageMessage
}