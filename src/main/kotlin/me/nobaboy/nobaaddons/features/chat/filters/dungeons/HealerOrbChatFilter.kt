package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.StatType
import me.nobaboy.nobaaddons.utils.NumberUtils.formatDouble
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object HealerOrbChatFilter : IChatFilter {
	private val otherPickupUserOrbPattern = Pattern.compile("◕ [A-z0-9_]+ picked up your [A-z ]+!")
	private val userPickupHealerOrbPattern = Pattern.compile(
		"^◕ You picked up a (?<orb>[A-z ]+) from (?<player>[A-z0-9_]+) healing you for (?<health>[0-9.]+)❤ and granting you (?<buff>[0-9+%]+) (?<stat>[A-z ]+) for (?<duration>[0-9]+) seconds\\."
	)

	override fun shouldFilter(message: Text, text: String): Boolean {
		val filterMode = config.healerOrbMessage

		userPickupHealerOrbPattern.matchMatcher(text) {
			if(filterMode == ChatFilterOption.COMPACT) {
				val statType = StatType.entries.firstOrNull { group("stat") == it.text || group("stat") == it.identifier } ?: return@matchMatcher
				val message = compileHealerOrbMessage(
					group("orb"), group("player"), group("health"), group("buff"), statType, group("duration")
				)
				ChatUtils.addMessage(message, false)
			}
			return true
		}

		return otherPickupUserOrbPattern.matches(text)
	}

	private fun compileHealerOrbMessage(
		orb: String,
		player: String,
		health: String,
		buff: String,
		statType: StatType,
		duration: String
	) = buildText {
		formatted(Formatting.GRAY)
		append(Text.literal("HEALER ORB!").formatted(Formatting.YELLOW, Formatting.BOLD))
		if(health.formatDouble() > 0.0) {
			append("+$health ")
			append(Text.literal("❤ Health").formatted(Formatting.RED))
			append(" and ")
		}
		append(" $buff ")
		append(statType.toText())
		append(" for $duration seconds from picking up $player's $orb.")
	}

	override fun isEnabled() = IslandType.DUNGEONS.inIsland() && isEnabled(config.healerOrbMessage)
}