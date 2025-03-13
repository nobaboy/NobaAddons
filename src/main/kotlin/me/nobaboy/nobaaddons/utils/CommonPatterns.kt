package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.repo.Repo.fromRepo

object CommonPatterns {
	const val UUID_PATTERN = "[0-9a-f]{8}-?(?:[0-9a-f]{4}-?){3}[0-9a-f]{12}"
	val UUID = Regex(UUID_PATTERN, RegexOption.IGNORE_CASE)

	// TODO: Change repo tag to skyblock.item_cooldown
	val ITEM_COOLDOWN_REGEX by Regex("^Cooldown: (?<cooldown>(?:\\d+[hms] ?)+)").fromRepo("item_cooldown")
}