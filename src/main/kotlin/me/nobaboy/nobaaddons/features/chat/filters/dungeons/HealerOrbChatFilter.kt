package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.NumberUtils.formatDouble
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object HealerOrbChatFilter : IChatFilter {
	private val otherPickupUserOrbPattern by Regex("◕ [A-z0-9_]+ picked up your [A-z ]+!").fromRepo("filter.healer_orb.other_pickup")
	private val userPickupHealerOrbPattern by Regex(
		"^◕ You picked up a (?<orb>[A-z ]+) from (?<player>[A-z0-9_]+) healing you for (?<health>[0-9.]+)❤ and granting you (?<buff>[0-9+%]+) (?<stat>[A-z ]+) for (?<duration>[0-9]+) seconds\\."
	).fromRepo("filter.healer_orb.pickup")

	override val enabled: Boolean get() = config.healerOrbMessage.enabled && SkyBlockIsland.DUNGEONS.inIsland()

	override fun shouldFilter(message: String): Boolean {
		val filterMode = config.healerOrbMessage

		userPickupHealerOrbPattern.onFullMatch(message) {
			if(filterMode == ChatFilterOption.COMPACT) {
				val statType = StatType.entries.firstOrNull {
					groups["stat"]!!.value == it.text || groups["stat"]!!.value == it.identifier
				} ?: return@onFullMatch
				val message = compileHealerOrbMessage(
					groups["orb"]!!.value, groups["player"]!!.value, groups["health"]!!.value, groups["buff"]!!.value, statType, groups["duration"]!!.value
				)
				ChatUtils.addMessage(message, prefix = false)
			}
			return true
		}

		return otherPickupUserOrbPattern.matches(message)
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
}