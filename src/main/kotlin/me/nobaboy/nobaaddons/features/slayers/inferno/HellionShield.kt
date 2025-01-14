package me.nobaboy.nobaaddons.features.slayers.inferno

import me.nobaboy.nobaaddons.utils.NobaColor

enum class HellionShield(val color: NobaColor) {
	SPIRIT(NobaColor.WHITE),
	CRYSTAL(NobaColor.AQUA),
	ASHEN(NobaColor.DARK_GRAY),
	AURIC(NobaColor.YELLOW);

	companion object {
		fun getByName(name: String): HellionShield? = entries.firstOrNull { name.contains(it.name) }
	}
}