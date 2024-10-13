package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.minecraft.scoreboard.ScoreboardDisplaySlot

object ScoreboardUtils {
	private val splitIcons = listOf(
		"\uD83C\uDF6B",
		"\uD83D\uDCA3",
		"\uD83D\uDC7D",
		"\uD83D\uDD2E",
		"\uD83D\uDC0D",
		"\uD83D\uDC7E",
		"\uD83C\uDF20",
		"\uD83C\uDF6D",
		"⚽",
		"\uD83C\uDFC0",
		"\uD83D\uDC79",
		"\uD83C\uDF81",
		"\uD83C\uDF89",
		"\uD83C\uDF82",
		"\uD83D\uDD2B",
	)

	fun String.cleanScoreboard() = this.cleanFormatting().filter { it.code in (21 until 127) }

	fun getSidebarLines(): List<String> {
		val lines = mutableListOf<String>()
		val player = NobaAddons.mc.player ?: return lines

		val scoreboard = player.scoreboard ?: return lines
		val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.FROM_ID.apply(1))

		scoreboard.knownScoreHolders.forEach { scoreHolder ->
			if(scoreboard.getScoreHolderObjectives(scoreHolder).contains(objective)) {
				val team = scoreboard.getScoreHolderTeam(scoreHolder.nameForScoreboard)

				if(team != null) {
					val line = team.prefix.string + team.suffix.string
					if(!line.trim().isEmpty()) lines.add(line.cleanFormatting())
				}
			}
		}

		lines.replaceAll { line ->
			var modifiedLine = line
			splitIcons.forEach { icon ->
				modifiedLine = modifiedLine.replace(icon, "")
			}
			modifiedLine
		}

		return lines
	}
}