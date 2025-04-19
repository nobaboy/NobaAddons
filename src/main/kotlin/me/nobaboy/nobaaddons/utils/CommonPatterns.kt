package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import org.intellij.lang.annotations.Language

object CommonPatterns {
	@Language("RegExp")
	const val PLAYER_NAME_WITH_RANK_STRING = "(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+)"

	@Language("RegExp")
	const val UUID_PATTERN_STRING = "[0-9a-f]{8}-?(?:[0-9a-f]{4}-?){3}[0-9a-f]{12}"

	val UUID_REGEX = Regex(UUID_PATTERN_STRING, RegexOption.IGNORE_CASE)

	val CHAT_COORDINATES_REGEX by Regex("(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+)(?<info>.*)").fromRepo("chat_coordinates")

	val ITEM_COOLDOWN_REGEX by Regex("^Cooldown: (?<cooldown>(?:\\d+[hms] ?)+)").fromRepo("skyblock.item_cooldown")

	val SLAYER_QUEST_COMPLETE_REGEX by Regex("^[ ]+SLAYER QUEST COMPLETE!").fromRepo("slayer.quest_complete")
	val SLAYER_BOSS_SLAIN_REGEX by Regex("^[ ]+NICE! SLAYER BOSS SLAIN!").fromRepo("slayer.boss_slain")
}