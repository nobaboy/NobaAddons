package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.ScoreboardEntry
import net.minecraft.scoreboard.Team

object ScoreboardUtils {
	private val SCOREBOARD_ENTRY_COMPARATOR = Comparator.comparing(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER)
	private val FORMATTING_CODE_REGEX = Regex("§.")

	fun getSidebarLines(): List<String> {
		val scoreboard = MCUtils.player?.scoreboard ?: return listOf()
		val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return listOf()

		return scoreboard.getScoreboardEntries(objective)
			.filter { !it.hidden() }
			.sortedWith(SCOREBOARD_ENTRY_COMPARATOR)
			.take(15)
			.mapNotNull { entry ->
				scoreboard.getScoreHolderTeam(entry.owner)?.let { team ->
					val line = Team.decorateName(team, entry.name()).string
						.cleanFormatting().trim().replace(FORMATTING_CODE_REGEX, "")

					line.takeIf { it.isNotEmpty() }
				}
			}
	}
}