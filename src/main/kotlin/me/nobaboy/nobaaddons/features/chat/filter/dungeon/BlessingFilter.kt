package me.nobaboy.nobaaddons.features.chat.filter.dungeon

import me.nobaboy.nobaaddons.api.SkyblockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filter.IFilter
import me.nobaboy.nobaaddons.features.chat.filter.StatType
import me.nobaboy.nobaaddons.utils.RegexUtils.findAllMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import me.nobaboy.nobaaddons.utils.StringUtils.startsWith
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object BlessingFilter : IFilter {
	private val config get() = NobaConfigManager.get().chat.filter

	private val blessingFindPattern = Pattern.compile(
		"^DUNGEON BUFF! ([A-z0-9_]+ found a|A) Blessing of (?<blessing>[A-z]+) [IV]+( was found)?!( \\([A-z0-9 ]+\\))?"
	)
	private val blessingStatsPattern = Pattern.compile(
		"(?<value>\\+[\\d.]+x?(?: & \\+[\\d.]+x?)?) (?<stat>❁ Strength|☠ Crit Damage|❈ Defense|❁ Damage|HP|❣ Health Regen|✦ Speed|✎ Intelligence)"
	)
	private val statMessages = listOf("     Granted you", "     Also granted you")

	private lateinit var blessingType: BlessingType
	private val stats: MutableList<Stat> = mutableListOf()

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ -> processMessage(message.string.clean()) }
	}

	fun processMessage(message: String): Boolean {
		if(!isEnabled()) return true
		val filterMode = config.blessingMessage

		blessingFindPattern.matchMatcher(message) {
			if(filterMode != ChatFilterOption.HIDDEN) {
				blessingType = BlessingType.valueOf(group("blessing").uppercase())
				stats.clear()
			}
			return false
		}

		if(message.startsWith(statMessages) && this::blessingType.isInitialized) {
			if(filterMode != ChatFilterOption.HIDDEN) {
				blessingStatsPattern.findAllMatcher(message) {
					val stat = StatType.entries.firstOrNull { group("stat") in it.identifiers } ?: return@findAllMatcher
					stats.add(Stat(stat, group("value")))
				}
				if(blessingType.expectedStats == stats.size) {
					ChatUtils.addMessage(compileBlessingMessage(), false)
				}
			}
			return false
		}

		return true
	}

	private fun compileBlessingMessage() = buildText {
		var previousValue: String? = null

		formatted(Formatting.GRAY)
		append(blessingType.toText())
		append(" ")
		stats.forEachIndexed { i, stat ->
			if (previousValue != stat.value) {
				previousValue = stat.value
				append(stat.value)
			}
			append(" ")
			append(stat.stat.toText())

			append(when(blessingType.expectedStats - i) {
				1 -> "."
				2 -> " and "
				else -> ", "
			})
		}
	}

	private class Stat(val stat: StatType, val value: String)

	private enum class BlessingType(val text: String, val color: Formatting, val expectedStats: Int) {
		POWER("POWER BUFF!", Formatting.RED, 2),
		WISDOM("WISDOM BUFF!", Formatting.BLUE, 2),
		STONE("STONE BUFF!", Formatting.DARK_GRAY, 2),
		LIFE("LIFE BUFF!", Formatting.LIGHT_PURPLE, 2),
		TIME("TIME BUFF!", Formatting.DARK_GREEN, 4);

		fun toText(): Text = text.toText().setStyle(color.bold())
	}

	private fun isEnabled() = IslandType.DUNGEONS.inIsland() && shouldFilter(config.blessingMessage)
}
