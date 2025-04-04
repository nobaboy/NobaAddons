package me.nobaboy.nobaaddons.features.chat.filters.dungeons

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.SkyBlockStat
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.startsWith
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object BlessingChatFilter : IChatFilter {
	private val BLESSING_FOUND_REGEX by Regex(
		"^DUNGEON BUFF! ([A-z0-9_]+ found a|A) Blessing of (?<blessing>[A-z]+) [IV]+( was found)?!( \\([A-z0-9 ]+\\))?"
	).fromRepo("filter.blessings.found")
	private val BLESSING_STATS_REGEX by Regex(
		"(?<value>\\+[\\d.]+x?(?: & \\+[\\d.]+x?)?) (?<stat>❁ Strength|☠ Crit Damage|❈ Defense|❁ Damage|HP|❣ Health Regen|✦ Speed|✎ Intelligence)"
	).fromRepo("filter.blessings.stats")

	private val statMessages = listOf("     Granted you", "     Also granted you")

	private var blessingType: BlessingType? = null
	private val blessingStats = mutableListOf<BlessingStat>()

	override val enabled: Boolean get() = config.blessingMessage.enabled && SkyBlockIsland.DUNGEONS.inIsland()

	override fun shouldFilter(message: String): Boolean {
		val filterMode = config.blessingMessage

		BLESSING_FOUND_REGEX.onFullMatch(message) {
			if(filterMode == ChatFilterOption.COMPACT) {
				blessingType = BlessingType.valueOf(groups["blessing"]!!.value.uppercase())
				blessingStats.clear()
			}
			return true
		}

		if(message.startsWith(statMessages)) {
			val blessingType = this.blessingType
			if(blessingType == null) {
				ErrorManager.logError("Found blessing stats messages before the blessing type", Error())
				return false
			}
			if(filterMode == ChatFilterOption.COMPACT) {
				println(message)

				BLESSING_STATS_REGEX.forEachMatch(message) {
					val stat = groups["stat"]?.value ?: return@forEachMatch
					val type = SkyBlockStat.entries.firstOrNull {
						stat == it.prefixedName || stat in it.aliases
					} ?: return@forEachMatch

					blessingStats.add(BlessingStat(type, groups["value"]!!.value))
				}

				when {
					blessingStats.size == blessingType.expectedStats -> {
						ChatUtils.addMessage(compileBlessingMessage(), prefix = false)
						this.blessingType = null
					}
					blessingStats.size > blessingType.expectedStats -> ErrorManager.logError(
						"Found more stats from a blessing than expected", Error(),
						"Blessing type" to blessingType,
						"Expected stats" to blessingType.expectedStats,
						"Found stats" to blessingStats
					)
					else -> NobaAddons.LOGGER.warn(
						// this doesn't use ErrorManager as it's possible that this is a blessing
						// which splits its stats into two messages, and we don't want to be overly noisy with
						// such blessings.
						"Found less stats than expected from {} blessing! Expected {}, but got {}",
						blessingType, blessingType.expectedStats, blessingStats.size
					)
				}
			}
			return true
		}

		return false
	}

	private fun compileBlessingMessage() = buildText {
		val blessingType = this@BlessingChatFilter.blessingType ?: throw IllegalStateException("blessingType is null")
		var previousValue: String? = null

		formatted(Formatting.GRAY)
		append(blessingType.formattedName)
		append(" ")
		blessingStats.forEachIndexed { i, blessingStat ->
			if(previousValue != blessingStat.value) {
				previousValue = blessingStat.value
				append(blessingStat.value)
				append(" ")
			}
			append(blessingStat.stat.displayName)

			append(
				when(blessingType.expectedStats - i) {
					1 -> "."
					2 -> " and "
					else -> ", "
				}
			)
		}
	}

	private enum class BlessingType(color: NobaColor, val expectedStats: Int) : NameableEnum {
		POWER(NobaColor.RED, 2),
		WISDOM(NobaColor.BLUE, 2),
		STONE(NobaColor.DARK_GRAY, 2),
		LIFE(NobaColor.LIGHT_PURPLE, 2),
		TIME(NobaColor.DARK_GREEN, 4);

		val formattedName: Text by lazy { displayName.copy().append(" BUFF!").formatted(color.formatting, Formatting.BOLD) }

		override fun getDisplayName(): Text = when(this) {
			POWER -> tr("nobaaddons.label.blessingType.power", "POWER")
			WISDOM -> tr("nobaaddons.label.blessingType.wisdom", "WISDOM")
			STONE -> tr("nobaaddons.label.blessingType.stone", "STONE")
			LIFE -> tr("nobaaddons.label.blessingType.life", "LIFE")
			TIME -> tr("nobaaddons.label.blessingType.time", "TIME")
		}
	}

	private data class BlessingStat(val stat: SkyBlockStat, val value: String)
}
