package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.repo.Repo

@Suppress("RegExpSimplifiable")
object CommonPatterns {
	const val UUID_PATTERN_STRING = "[0-9a-f]{8}-?(?:[0-9a-f]{4}-?){3}[0-9a-f]{12}"
	val UUID_REGEX = Regex(UUID_PATTERN_STRING, RegexOption.IGNORE_CASE)

	val CHAT_COORDINATES_REGEX by Repo.regex(
		"chat_coordinates",
		"(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+)(?<info>.*)"
	)

	val ITEM_COOLDOWN_REGEX by Repo.regex("skyblock.item_cooldown", "^Cooldown: (?<cooldown>(?:\\d+[hms] ?)+)")

	val SLAYER_QUEST_COMPLETE_REGEX by Repo.regex("slayer.quest_complete", "^[ ]+SLAYER QUEST COMPLETE!")
	val SLAYER_BOSS_SLAIN_REGEX by Repo.regex("slayer.boss_slain", "^[ ]+NICE! SLAYER BOSS SLAIN!")
}