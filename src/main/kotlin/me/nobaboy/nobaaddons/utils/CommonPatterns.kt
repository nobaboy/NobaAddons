package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.repo.Repo.fromRepo

object CommonPatterns {
	const val UUID_STRING = "[0-9a-f]{8}-?(?:[0-9a-f]{4}-?){3}[0-9a-f]{12}"
	val UUID_REGEX = Regex(UUID_STRING, RegexOption.IGNORE_CASE)

	val CHAT_COORDINATES_REGEX by Regex("(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+)(?<info>.*)").fromRepo("chat_coordinates")

	// TODO: Move under skyblock tag in repo
	val ITEM_COOLDOWN_REGEX by Regex("^Cooldown: (?<cooldown>(?:\\d+[hms] ?)+)").fromRepo("item_cooldown")
}