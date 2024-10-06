package me.nobaboy.nobaaddons.features.chat.filter.dungeon

import me.nobaboy.nobaaddons.api.SkyblockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filter.IFilter
import me.nobaboy.nobaaddons.features.chat.filter.StatType
import me.nobaboy.nobaaddons.utils.NumberUtils.formatDouble
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object HealerOrbFilter : IFilter {
	private val config get() = NobaConfigManager.get().chat.filter

	private val otherPickupUserOrbPattern = Pattern.compile("◕ [A-z0-9_]+ picked up your [A-z ]+!")
	private val userPickupHealerOrbPattern = Pattern.compile(
		"^◕ You picked up a (?<orb>[A-z ]+) from (?<player>[A-z0-9_]+) healing you for (?<health>[0-9.]+)❤ and granting you (?<buff>[0-9+%]+) (?<stat>[A-z ]+) for (?<duration>[0-9]+) seconds\\."
	)

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ -> processMessage(message.string.clean()) }
	}

	private fun processMessage(message: String): Boolean {
		if(!isEnabled()) return true
		val filterMode = config.healerOrbMessage

		userPickupHealerOrbPattern.matchMatcher(message) {
			if(filterMode != ChatFilterOption.HIDDEN) {
				val statType = StatType.entries.firstOrNull { group("stat") in it.identifiers } ?: return@matchMatcher

				val message = compileHealerOrbMessage(
					group("orb"), group("player"), group("health"), group("buff"), statType, group("duration")
				)
				ChatUtils.addMessage(message, false)
			}
			return false
		}

		otherPickupUserOrbPattern.matchMatcher(message) {
			return false
		}

		return true
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
		append("HEALER ORB! ".toText().formatted(Formatting.YELLOW, Formatting.BOLD))
		if (health.formatDouble() > 0.0) {
			append("+$health ")
			append("❤ Health".toText().formatted(Formatting.RED))
			append(" and ")
		}
		append(buff)
		append(" ")
		append(statType.toText())
		append(" for $duration seconds from picking up $player's $orb.")
	}

	private fun isEnabled() = IslandType.DUNGEONS.inIsland() && shouldFilter(config.healerOrbMessage)
}