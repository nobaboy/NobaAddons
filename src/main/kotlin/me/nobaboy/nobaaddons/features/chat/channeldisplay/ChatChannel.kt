package me.nobaboy.nobaaddons.features.chat.channeldisplay

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.withColor

enum class ChatChannel(val color: NobaColor) {
	ALL(NobaColor.GRAY),
	PARTY(NobaColor.BLUE),
	GUILD(NobaColor.DARK_GREEN),
	DM(NobaColor.GOLD),
	UNKNOWN(NobaColor.RED),
	;

	fun toText() = name.toText().withColor(color)

	companion object {
		fun fromString(channel: String) = entries.firstOrNull { it.name == channel } ?: UNKNOWN
	}
}