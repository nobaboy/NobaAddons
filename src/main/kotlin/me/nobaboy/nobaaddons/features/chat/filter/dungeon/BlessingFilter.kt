package me.nobaboy.nobaaddons.features.chat.filter.dungeon

import me.nobaboy.nobaaddons.api.SkyblockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filter.IFilter
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
		"(?<one>\\+[\\d.]+x?)( & )?(?<two>\\+[\\d.]+x?)? (?<stat>❁ Strength|☠ Crit Damage|❈ Defense|❁ Damage|HP|❣ Health Regen|✦ Speed|✎ Intelligence)"
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
				blessingStatsPattern.matchMatcher(message) {
					val stat = StatType.entries.firstOrNull { it.text == group("stat") } ?: return@matchMatcher
					stats.add(Stat(stat, group("one")!!, group("two")))
				}
				if(blessingType.expectedStats == stats.size) {
					ChatUtils.addMessage(compileBlessingMessage(), false, overlay = filterMode == ChatFilterOption.ACTION_BAR)
				}
			}
			return false
		}

		return true
	}

	private fun compileBlessingMessage() = buildText {
		append(blessingType.toText())
		append(" ")
		stats.forEachIndexed { i, stat ->
			append(stat.one)
			stat.two?.let { append(" & $it") }
			append(" ")
			append(stat.stat.toText())

			append(when(blessingType.expectedStats - i) {
				1 -> "."
				2 -> " and "
				else -> ", "
			})
		}
	}

	private class Stat(val stat: StatType, val one: String, val two: String?)

	private enum class StatType(val text: String, val color: Formatting) {
		STRENGTH("❁ Strength", Formatting.RED),
		CRIT_DAMAGE("☠ Crit Damage", Formatting.BLUE),
		DEFENSE("❈ Defense", Formatting.GREEN),
		DAMAGE("❁ Damage", Formatting.RED),
		HEALTH("HP", Formatting.RED),
		HEALTH_REGEN("❣ Health Regen", Formatting.RED),
		SPEED("✦ Speed", Formatting.WHITE),
		INTELLIGENCE("✎ Intelligence", Formatting.AQUA);

		fun toText(): Text = text.toText().formatted(color)
	}

	private enum class BlessingType(val text: String, val color: Formatting, val expectedStats: Int = 1) {
		POWER("POWER BUFF!", Formatting.RED, expectedStats = 2),
		WISDOM("WISDOM BUFF!", Formatting.BLUE),
		STONE("STONE BUFF!", Formatting.DARK_GRAY),
		LIFE("LIFE BUFF!", Formatting.LIGHT_PURPLE),
		TIME("TIME BUFF!", Formatting.DARK_GREEN);

		fun toText(): Text = text.toText().setStyle(color.bold())
	}

	private fun isEnabled() = IslandType.DUNGEONS.inIsland() && shouldFilter(config.blessingMessage)
}
