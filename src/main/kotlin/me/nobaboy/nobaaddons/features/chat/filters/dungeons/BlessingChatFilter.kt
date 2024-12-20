package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.StatType
import me.nobaboy.nobaaddons.utils.RegexUtils.findAllMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.startsWith
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.formatted
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object BlessingChatFilter : IChatFilter {
	private val blessingFindPattern = Pattern.compile(
		"^DUNGEON BUFF! ([A-z0-9_]+ found a|A) Blessing of (?<blessing>[A-z]+) [IV]+( was found)?!( \\([A-z0-9 ]+\\))?"
	)
	private val blessingStatsPattern = Pattern.compile(
		"(?<value>\\+[\\d.]+x?(?: & \\+[\\d.]+x?)?) (?<stat>❁ Strength|☠ Crit Damage|❈ Defense|❁ Damage|HP|❣ Health Regen|✦ Speed|✎ Intelligence)"
	)
	private val statMessages = listOf("     Granted you", "     Also granted you")

	private lateinit var blessingType: BlessingType
	private val stats = mutableListOf<Stat>()

	override fun shouldFilter(message: String): Boolean {
		val filterMode = config.blessingMessage

		blessingFindPattern.matchMatcher(message) {
			if(filterMode == ChatFilterOption.COMPACT) {
				blessingType = BlessingType.valueOf(group("blessing").uppercase())
				stats.clear()
			}
			return true
		}

		if(message.startsWith(statMessages)) {
			check(this::blessingType.isInitialized) { "Blessing type is not set!" }
			if(filterMode == ChatFilterOption.COMPACT) {
				blessingStatsPattern.findAllMatcher(message) {
					val statType = StatType.entries.firstOrNull { group("stat") == it.text || group("stat") == it.identifier } ?: return@findAllMatcher
					stats.add(Stat(statType, group("value")))
				}

				when {
					stats.size == blessingType.expectedStats -> ChatUtils.addMessage(compileBlessingMessage(), false)
					stats.size > blessingType.expectedStats -> NobaAddons.LOGGER.warn(
						"Found more stats than expected from {} blessing! Expected {}, but got {}",
						blessingType, blessingType.expectedStats, stats.size
					)
					else -> NobaAddons.LOGGER.warn(
						"Found less stats than expected from {} blessing! Expected {}, but got {}",
						blessingType, blessingType.expectedStats, stats.size
					)
				}
			}
			return true
		}

		return false
	}

	private fun compileBlessingMessage() = buildText {
		var previousValue: String? = null

		formatted(Formatting.GRAY)
		append(blessingType.toText())
		append(" ")
		stats.forEachIndexed { i, stat ->
			if(previousValue != stat.value) {
				previousValue = stat.value
				append(stat.value)
				append(" ")
			}
			append(stat.statType.toText())

			append(
				when(blessingType.expectedStats - i) {
					1 -> "."
					2 -> " and "
					else -> ", "
				}
			)
		}
	}

	private class Stat(val statType: StatType, val value: String)

	private enum class BlessingType(val text: String, val color: Formatting, val expectedStats: Int) {
		POWER("POWER BUFF!", Formatting.RED, 2),
		WISDOM("WISDOM BUFF!", Formatting.BLUE, 2),
		STONE("STONE BUFF!", Formatting.DARK_GRAY, 2),
		LIFE("LIFE BUFF!", Formatting.LIGHT_PURPLE, 2),
		TIME("TIME BUFF!", Formatting.DARK_GREEN, 4);

		fun toText(): Text = text.formatted(color).bold()
	}

	override fun isEnabled() = SkyBlockIsland.DUNGEONS.inIsland() && isEnabled(config.blessingMessage)
}
