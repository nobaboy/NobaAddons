package me.nobaboy.nobaaddons.features.chat.channeldisplay

import me.nobaboy.nobaaddons.utils.NobaColor

enum class ChatChannel(val color: NobaColor) {
	ALL(NobaColor.GRAY),
	PARTY(NobaColor.BLUE),
	GUILD(NobaColor.DARK_GREEN),
	DM(NobaColor.GOLD),
	UNKNOWN(NobaColor.RED),
	;

	companion object {
		fun fromString(channel: String) = entries.firstOrNull { it.name == channel } ?: UNKNOWN
	}
}